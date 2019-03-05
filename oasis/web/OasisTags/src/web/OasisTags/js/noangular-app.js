
define(['require', 'bootstrap', 'jqx-widgets'],
    function (require, bootstrap, jqxWidgets) {


        dti = (typeof dti != "undefined") ?  dti : {};

        dti.globalApp = (function() {
            var construct = function(constructor, args) {
                function F() {
                    return constructor.apply(this, args);
                }
                F.prototype = constructor.prototype;
                return new F();
            };

            var updateAddressBar = function($state) {
                // replace filename if running as a test project.  "ex. /index.html"
                //var current = dti.globalApp.$context.$default.url.replace(/([^\/]+)\.[^\/]+$/,'');
                var current = dti.globalApp.$context.$default.url;
                var urlParts = current.split('#');
                if (urlParts.length > 1) {
                    urlParts[1] = $state.url;
                }
                else {
                    urlParts.push($state.url);
                }
                current = urlParts.join('#');
                history.pushState({}, null, current);
            };

            var getState = function(context, urlPiece) {
                var route, states = context.$states;
                for (var key in states) {
                    var state = states[key];
                    var matchInfo = urlMatches(state.url, urlPiece);
                    if (matchInfo.isMatch) {
                        route = $.extend( true, {}, state ); // perform a deep copy
                        route.url = urlPiece;
                        route.params = $.extend( true, {}, matchInfo.params );
                        break;
                    }
                }

                var newState = route || context.$states[context.$default.$state];
                return newState;
            };


            var urlMatches = function(statePiece, urlPiece) {
                var matchInfo = {isMatch: false, params: {}};
                if (!urlPiece) {
                    return matchInfo;
                }

                var stateParts = statePiece.split('/');
                var urlParts = urlPiece.split('/');
                if (stateParts.length == urlParts.length) {
                    if (stateParts[1] == urlParts[1]) {
                        matchInfo.isMatch = true;
                        for (var i=2; i<urlParts.length; i++) {
                            var paramName = stateParts[i].replace(':','');
                            matchInfo.params[paramName] = urlParts[i];
                        }
                    }
                }

                return matchInfo;
            };

            var handleLinks = function() {
                $("a").off('click').on('click', function(e) {
                    var href = $(this).attr('href');
                    if (href && href.indexOf('#') > -1) {
                        var matchInfo = this.getStateKey(dti.globalApp.$context, href);
                        dti.globalApp.$context.transitionTo(matchInfo.key, matchInfo.params);
                    }
                    else {
                        location.href = href;
                    }
                });
            };

            return {
                    $components: {  // internal components that must be loaded and called
                    },
                    run: function() {
                        // load first page
                        var urlParts = location.href.split('#');
                        var context = this.$context;
                        context.$state = getState(context, urlParts[1]);

                        this.loadFullPage(context);
                    },
                    register: function(name, dependencies, component, type) {
                        dti.globalApp.$components[name] = {
                            dep: dependencies,
                            comp: component,
                            type: type
                        };
                    },
                    controller: function(name, dep) {
                        var controller = dep.pop(); // last item is the controller
                        dti.globalApp.register(name, dep, controller, "controller");
                    },
                    service: function(name, dep) {
                        var service = dep.pop();
                        dti.globalApp.register(name, dep, service, "service");
                    },
                    factory: function(name, dep) {
                        var factory = dep.pop();
                        dti.globalApp.register(name, dep, factory, "factory");
                    },
                    loadView: function(templateUrl, selector, onViewLoaded) {
                        var sel = selector || ".dti-ui-view";
                        jQuery(sel).load(templateUrl, onViewLoaded);
                        jQuery(sel).removeClass("invisible");
                    },
                    loadFullPage: function(context) {  // load all views and controllers
                        var state = context.$state;

                        if (!state) {
                            console.log("loadFullPage: context.$state is undefined");
                            return;
                        }
                        var atLeastOneViewIsFound = false;

                        for (viewName in state.views) {
                            var dependencies = [state.views[viewName].controller];
                            var currentPartialPage = state.views[viewName];
                            currentPartialPage.selector = "#" + viewName;

                            if ($(currentPartialPage.selector).length == 0) {
                                continue;   // element is not found
                            }
                            atLeastOneViewIsFound = true;

                            if (!currentPartialPage.invisible) {
                                var previousView = (context.$previousState) ? context.$previousState.views[viewName] : null;
                                var currentView = state.views[viewName];

                                // load new page if controller has changed or on the first run
                                if (!context.$previousState) {
                                    (function(state, currentPartialPage) {
                                        return dti.globalApp.loadPage(context, state, currentPartialPage, dependencies);
                                    })(state, currentPartialPage, dependencies);
                                }
                                else if (currentView.controller != previousView.controller){
                                    (function(state, currentPartialPage) {
                                        return dti.globalApp.loadPage(context, state, currentPartialPage, dependencies);
                                    })(state, currentPartialPage, dependencies);
                                }
                                else {
                                    currentView.controllerInstance = previousView.controllerInstance;
                                    if (currentView.controllerInstance.onPageStart) { // re-use controller
                                        currentView.controllerInstance.onPageStart(dti.globalApp.$context);
                                    }
                                }
                            }
                            else {
                                $(currentPartialPage.selector).addClass("invisible");
                            }
                        }

                        if (atLeastOneViewIsFound) {
                            updateAddressBar(state);

                            $.event.trigger({
                                type: "globalApp-viewsLoaded",
                                message: "globalApp-viewsLoaded",
                                time: new Date()
                            });
                        }
                        else {
                            console.log("loadFullPage: No matching view selectors found for " + state.url + ". Check routing setup");
                        }

                    },
                    loadPage: function(context, state, currentPartialPage, dependencies) {
                        require(dependencies, function(controller){ // ** pull in the controller; let define pull in dependencies
                            dti.globalApp.loadView(                 // ** load the view
                                currentPartialPage.templateUrl,
                                currentPartialPage.selector,
                                function () {
                                    var skipController = false;
                                    if (state.onViewLoaded) {
                                        skipController = state.onViewLoaded();
                                    }

                                    if (!skipController) {

                                        var component = dti.globalApp.$components[currentPartialPage.controller];
                                        require(component.dep, function() {     // ** load controller dependencies
                                            currentPartialPage.controllerInstance = null;

                                            // create new instances of each service
                                            var args = Array.prototype.slice.call(arguments);
                                            for (var i=0; i<args.length; i++) {
                                                var fn = args[i];
                                                var constr = new fn();  // TODO: support service dependencies
                                                args[i] = constr;
                                            }

                                            // add context to arguments
                                            args.unshift(context);

                                            // send to constructor
                                            currentPartialPage.controllerInstance = construct(controller, args);
                                            if (currentPartialPage.controllerInstance.onPageStart) {
                                                currentPartialPage.controllerInstance.onPageStart(dti.globalApp.$context);
                                            }

                                            handleLinks();
                                        });
                                    }
                                }

                            );
                        });
                    },
                    getStateKey: function(context, urlPiece) {
                        var matchInfo;
                        var route, states = context.$states;
                        for (var key in states) {
                            var state = states[key];
                            matchInfo = urlMatches(state.url, urlPiece);
                            if (matchInfo.isMatch) {
                                matchInfo.key = key;
                                return matchInfo;
                            }
                        }

                        return matchInfo;
                    }


        }
        })();

        dti.globalApp.$context = {
            transitionTo: function(name, params) {
                // close out the current state
                var state = this.$state;
                for (viewName in state.views) {
                    var currentPartialPage = state.views[viewName];
                    currentPartialPage.selector = "#" + viewName;

                    if (currentPartialPage.controllerInstance &&
                        currentPartialPage.controllerInstance.onPageEnd) {
                        (function(pg) {
                            return pg.controllerInstance.onPageEnd(this);
                        })(currentPartialPage);
                    }
                }

                // clear out previous state and save this one (make sure no memory leaks)
                this.$previousState = this.$state;
                //this.$previousState.controllerInstance = null;

                // find state
                var route, states = this.$states;
                for (var key in states) {
                    if (key.toUpperCase() == name.toUpperCase()) {
                        route = $.extend( true, {}, states[key] ); // perform a deep copy
                        var urlPart = route.url.split("/:")[0];
                        var newParts = [urlPart];
                        for (var p in params) {
                            newParts.push(params[p]);
                        }
                        var newUrl = newParts.join("/");
                        route.url = newUrl;
                        route.params = $.extend( true, {}, params );
                        break;
                    }
                }

                if (!route) {
                    if (this.otherwiseFn) {
                        this.otherwiseFn(name, params);
                    }
                }
                var newRoute = route || this.$states[this.$default.$state];

                if (newRoute) {
                    this.$state = newRoute;
                    dti.globalApp.loadFullPage(this);
                }
                else {
                    console.log("dti.globalApp.$context.transitionTo: '" + name + "' not found in configuration")
                }

            },
            go: function(href) {
                var matchInfo = dti.globalApp.getStateKey(dti.globalApp.$context, href);
                dti.globalApp.$context.transitionTo(matchInfo.key, matchInfo.params);
            },
            otherwise: function(otherwiseFn) {
                if (otherwiseFn) {
                    this.otherwiseFn = otherwiseFn;
                }
            },
            set: function(name, value) {
                sessionStorage[name] = JSON.stringify(value);
            },
            get: function(name) {
                return JSON.parse(sessionStorage[name]);
            }
        };


        dti.globalApp.$context.$states = {
            "SearchProducts": {
                url: "/SearchProducts",
                views: {
                    "dynamic-view": {
                        templateUrl: "./ems/product/products.html",
                        controller: "searchProductController"
                    }
                }
            }
        };

        var loc = location.href.replace(/\/$/,"");
        dti.globalApp.$context.$default = { // a.k.a. ui.router.otherwise()
            $state: "SearchProducts",
            url: loc + ((loc.indexOf("#") == -1) ? "#" : "")
        };

        $(document).on("globalApp-viewsLoaded", function (evt) {
            setTimeout(function() { // TODO: change to event based instead of timing
                $("a[ui-sref=searchProduct]").attr("href", "#/SearchProducts");
            }, 1000);

        });

        //dti.globalApp.run();  //TODO: Uncomment for noangualar support

    return  dti.globalApp;
});
