/************************************************************************
*************************************************************************
@Name    :      jMenu - jQuery Plugin
@Revison :      2.0
@Date    :      08/2013
@Author  :      ALPIXEL - (www.myjqueryplugins.com - www.alpixel.fr)
@Support :      FF, IE7, IE8, MAC Firefox, MAC Safari
@License :      Open Source - MIT License : http://www.opensource.org/licenses/mit-license.php

**************************************************************************
*************************************************************************/

/** jMenu Plugin **/
(function($) {
    $.jMenu = {
        /**************/
        /** OPTIONS **/
        /**************/
        defaults: {
            ulWidth:           'auto',
            absoluteTop:       33,
            absoluteLeft:      0,
            TimeBeforeOpening: 100,
            TimeBeforeClosing: 100,
            animatedText:      true,
            paddingLeft:       7,
            openClick:         false,
            effects: {
                effectSpeedOpen:  150,
                effectSpeedClose: 150,
                effectTypeOpen:   'slide',
                effectTypeClose:  'slide',
                effectOpen:       'swing',
                effectClose:      'swing'
            }
        },

        /*****************/
        /** Init Method **/
        /*****************/
        init: function(current, options) {
            /* vars **/
            opts = $.extend({}, $.jMenu.defaults, options);

            // Set global width of the sub-menus links
            if(opts.ulWidth == 'auto')
                $width = $(current).find(".fNiv").outerWidth(false);
            else
                $width = opts.ulWidth;



            $(current).find("li").each(function() {
                var
                    $thisChild = $(this).find('a:first'),
                    $allUl = $(this).find('ul');

                if($.jMenu._IsParent($thisChild))
                {
                    $thisChild.addClass('isParent');

                    var
                        $ul = $thisChild.next(),
                        $position = $thisChild.position();

                     // move to show function, so the position is calculated dynamically.
//                    if($(this).hasClass('jmenu-level-0'))
//                        $ul.css({
//                            top:   $position.top + opts.absoluteTop,
//                            left:  $position.left + opts.absoluteLeft,
//                            width : $width
//                        });
//                    else
//                        $ul.css({
//                            top:   $position.top,
//                            left:  $position.left + $width,
//                            width : $width
//                        });


                    if(!opts.openClick)
                        $(this).bind({
                            mouseenter:function() {
                                $.jMenu._show($ul);
                            },
                            mouseleave:function(){
                                $.jMenu._closeList($ul);
                            }
                        });
                    else
                        $(this).bind({
                            click:function(e) {
                                e.preventDefault();
                                $.jMenu._show($ul);
                            },
                            mouseleave:function(){
                                $.jMenu._closeList($ul);
                            }
                        });
                }
            });
        },


        /****************************
        *****************************
        ** jMenu Methods Below     **
        *****************************
        ****************************/
        _show: function(el) {

            /*** BEGINNING of calculating position dynamically **/
            var $ul = el;
            var liObject = el.parent();
            var $position = liObject.position();

            if(liObject.hasClass('jmenu-level-0'))
                $ul.css({
                    top:   $position.top + liObject.height,
                    left:  $position.left + opts.absoluteLeft
//                    width : $width
                });
            else
                $ul.css({
                    top:   0,
                    left:  $position.left + liObject.css("width")
//                    width : $width
                });
            /*** END of calculating position dynamically **/

            /*** BEGINNING of using iFrame **/
            // When there is activeX control, we need to use iFrame to make is display on top of the actionX control
            // Example: maintain form letters page
            // To turn on it, add function hasActiveXControl on page and return true.
            if (window.hasActiveXControl && window.hasActiveXControl() == true) {
                if (!window.isInSunMenuIframe) {
                    if ($ul.children("iframe").length == 0) {
                        var newiframe = document.createElement("iframe");
                        newiframe.frameBorder = "no";
                        newiframe.scrolling = "no";
                        newiframe.style.width = $ul.children("li").outerWidth();
                        newiframe.style.height = $ul.height();
                        newiframe.style.overflow = "hidden";
                        newiframe.style.border = "0px";
                        newiframe.style.margin = "0px";
                        newiframe.style.padding = "0px";
                        newiframe.style.position = "absolute";
                        newiframe.style.top = "0";
                        newiframe.style.left = "0";
                        $ul.append(newiframe);
                        newiframe.src = getCorePath() + "/subMenuIframe.jsp?date=" + new Date();
                    }
                }
            }
            /*** END of using iFrame **/

            switch(opts.effects.effectTypeOpen) {
                case 'slide':
                    el.stop(true, true).delay(opts.TimeBeforeOpening).slideDown(opts.effects.effectSpeedOpen, opts.effects.effectOpen);
                    break;
                case 'fade':
                    el.stop(true, true).delay(opts.TimeBeforeOpening).fadeIn(opts.effects.effectSpeedOpen, opts.effects.effectOpen);
                    break;
                default:
                    el.stop(true, true).delay(opts.TimeBeforeOpening).show();
            }
        },

        _closeList: function(el) {
            switch(opts.effects.effectTypeClose) {
                case 'slide':
                    el.stop(true,true).delay(opts.TimeBeforeClosing).slideUp(opts.effects.effectSpeedClose, opts.effects.effectClose);
                    break;
                case 'fade':
                    el.stop(true,true).delay(opts.TimeBeforeClosing).fadeOut(opts.effects.effectSpeedClose, opts.effects.effectClose);
                    break;
                default:
                    el.delay(opts.TimeBeforeClosing).hide();
            }
        },

        _animateText: function(el) {
            var paddingInit = parseInt(el.css('padding-left'));
            el.hover(
                function() {
                    $(this).stop(true,false).animate({paddingLeft: paddingInit + opts.paddingLeft}, 100);
                },
                function() {
                    $(this).stop(true,false).animate({paddingLeft:paddingInit}, 100);
                }
            );
        },

        _IsParent: function(el) {
            if (el.next().is('ul'))
                return true;
            else
                return false;
        },

        _isReadable: function() {
            if ($(".jmenu-level-0").length > 0)
                return true;
            else
                return false;
        },

        _error: function() {
            alert('jMenu plugin can\'t be initialized. Please, check you have the \'.jmenu-level-0\' class on your first level <li> elements.');
        }
    };

    jQuery.fn.jMenu = function(options){

        // Generate the class in OasisGlobalNav.java/OasisTabMenu.java, so we don't see tree nodes
        //$(this).addClass('jMenu');

        $(this)
            .children('li').addClass('jmenu-level-0')
            .children('a').addClass('fNiv');

        if($.jMenu._isReadable()) {
            $.jMenu.init(this, options);
        } else {
            $.jMenu._error();
        }
    };
})(jQuery);
