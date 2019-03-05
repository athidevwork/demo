/*
 jQWidgets v4.5.0 (2017-Jan)
 Copyright (c) 2011-2017 jQWidgets.
 License: http://jqwidgets.com/license/
*/
(function(d){d.jqx.dataview.sort=function(){this.sortby=function(b,a,f){var p=Object.prototype.toString;if(null==a)this.sortdata=null,this.sortcache={},this.grid._pagescache=[],this.grid._cellscache=[],this.refresh(null,{skipFilters:!0});else{void 0==a&&(a=!0);a="a"==a||"asc"==a||"ascending"==a||1==a?!0:!1;var c=b;this.sortfield=b;this.sortfielddirection=a?"asc":"desc";void 0==this.sortcache&&(this.sortcache={});this.sortdata=[];var e=[],m=!1;"constructor"==c&&(c="");if(!this.virtualmode&&null!=this.sortcache[c]){var g=
this.sortcache[c],e=g._sortdata;g.direction==a?e.reverse():(!g.direction&&a&&e.reverse(),m=!0);e.length<this.totalrecords&&(this.sortcache={},m=!1,e=[])}Object.prototype.toString="function"==typeof b?b:function(){return this[b]};var g=this.records,n="";this.source.datafields&&d.each(this.source.datafields,function(){if(this.name==b)return this.type&&(n=this.type),!1});if(0==e.length)if(g.length)for(var k=g.length,l=0;l<k;l++){var h=g[l];null!=h&&e.push({sortkey:h.toString(),value:h,index:l})}else{k=
!1;for(obj in g){h=g[obj];if(void 0==h){k=!0;break}e.push({sortkey:h.toString(),value:h,index:obj})}k&&d.each(g,function(a,b){e.push({sortkey:b.toString(),value:b,index:a})})}if(!m)if(null==f){this._sortcolumntype=n;var q=this;e.sort(function(a,b){return q._compare(a,b,n)})}else e.sort(f);a||e.reverse();Object.prototype.toString=p;this.sortdata=e;this.sortcache[c]={_sortdata:e,direction:a};this.reload(this.records,this.rows,this.filters,this.updated,!0)}};this.clearsortdata=function(){this.sortcache=
{};this.sortdata=null};this._compare=function(b,a,f){b=b.sortkey;a=a.sortkey;if(void 0===b||""===b)b=null;if(void 0===a||""===a)a=null;if(null===b&&null===a)return 0;if(null===b&&null!==a)return-1;if(null!==b&&null===a)return 1;if(d.jqx.dataFormat)if(f&&""!=f)switch(f){case "string":case "text":b=String(b).toLowerCase(),a=String(a).toLowerCase()}else{if(d.jqx.dataFormat.isNumber(b)&&d.jqx.dataFormat.isNumber(a)||d.jqx.dataFormat.isDate(b)&&d.jqx.dataFormat.isDate(a))return b<a?-1:b>a?1:0;d.jqx.dataFormat.isNumber(b)||
d.jqx.dataFormat.isNumber(a)||(b=String(b).toLowerCase(),a=String(a).toLowerCase())}return b<a?-1:b>a?1:0};this._equals=function(b,a){return 0===this._compare(b,a)}};d.extend(d.jqx._jqxGrid.prototype,{_rendersortcolumn:function(){var b=this.that,a=this.getsortcolumn();if(this.sortdirection){var f=function(a,c){var e=b.getcolumn(a);e&&(c.ascending?d.jqx.aria(e.element,"aria-sort","ascending"):c.descending?d.jqx.aria(e.element,"aria-sort","descending"):d.jqx.aria(e.element,"aria-sort","none"))};this._oldsortinfo&&
this._oldsortinfo.column&&f(this._oldsortinfo.column,{ascending:!1,descending:!1});f(a,this.sortdirection)}this._oldsortinfo={column:a,direction:this.sortdirection};this.sortdirection&&d.each(this.columns.records,function(f,c){var e=d.data(document.body,"groupsortelements"+this.displayfield);null==a||this.displayfield!=a?(d(this.sortasc).hide(),d(this.sortdesc).hide(),null!=e&&(e.sortasc.hide(),e.sortdesc.hide())):b.sortdirection.ascending?(d(this.sortasc).show(),d(this.sortdesc).hide(),null!=e&&
(e.sortasc.show(),e.sortdesc.hide())):(d(this.sortasc).hide(),d(this.sortdesc).show(),null!=e&&(e.sortasc.hide(),e.sortdesc.show()))})},getsortcolumn:function(){return void 0!=this.sortcolumn?this.sortcolumn:null},removesort:function(){this.sortby(null)},sortby:function(b,a,f,d,c){if(this._loading&&!1!==c)throw Error("jqxGrid: "+this.loadingerrormessage);null==b&&(a=null,b=this.sortcolumn);if(void 0!=b){c=this.that;void 0==f&&null!=c.source.sortcomparer&&(f=c.source.sortcomparer);ascending="a"==a||
"asc"==a||"ascending"==a||1==a?!0:!1;c.sortdirection=null!=a?{ascending:ascending,descending:!ascending}:{ascending:!1,descending:!1};c.sortcolumn=null!=a?b:null;if(c.source.sort||c.virtualmode){if(c.dataview.sortfield=b,c.dataview.sortfielddirection=null==a?"":ascending?"asc":"desc",c.source.sort&&!this._loading){c.source.sort(b,a);c._raiseEvent(6,{sortinformation:c.getsortinformation()});return}}else c.dataview.sortby(b,a,f);!1!==d&&(c.groupable&&0<c.groups.length?(c._render(!0,!1,!1),c._updategroupheadersbounds&&
c.showgroupsheader&&c._updategroupheadersbounds()):(c.pageable&&c.dataview.updateview(),c._updaterowsproperties(),c.rendergridcontent(!0)),c._postrender("sort"),c._raiseEvent(6,{sortinformation:c.getsortinformation()}))}},_togglesort:function(b){var a=this.that;if(!this.disabled&&b.sortable&&a.sortable){var d=a.getsortinformation();null!=d.sortcolumn&&d.sortcolumn==b.displayfield?(d=d.sortdirection.ascending,d=1<a.sorttogglestates?1==d?!1:null:!d):d=!0;a.sortby(b.displayfield,d,null)}}})})(jqxBaseFramework);
