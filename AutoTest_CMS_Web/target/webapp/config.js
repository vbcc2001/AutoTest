var _sysVersion = "1.0.5"; //定义版本号  
var _project_configuration_path = "scripts/config.js"; //定义配置文件的路径
var _headerPage = {"pageCode": "/include/header", "jsonParam":{},"isLoad":false,"loadType":"0"}; //通用页头
var _footerPage = {"pageCode": "/include/footer", "jsonParam":{},"isLoad":false,"loadType":"0"}; //通用页尾
var _loginFlag = "0"; // 0不检测登陆 1检测登陆
var _curruct_path =  window.location.pathname.substring(window.location.pathname.indexOf('/web/html')+9,window.location.pathname.lastIndexOf('.html'));
var _defaultPage = {"pageCode":_curruct_path, "jsonParam":{},"isLoad":false,"loadType":"1"}; //通用js
var _is_login = false;
var _myTabbar;
//var _hmt = _hmt || [];
// (function() {
//   var hm = document.createElement("script");
//   hm.src = "sea.js";
//   var s = document.getElementsByTagName("head"); 
//   s.insertBefore(hm, s);
// })();
// seajs.config({
// alias: {
//   "user_choose":"js/common/user_choose.js"
// }});
// function loadJs(file) {
//   var head = $("head").remove("script[role='reload']");
//   $("<scri" + "pt>" + "</scr" + "ipt>").attr({ role: 'reload', src: file, type: 'text/javascript' }).appendTo(head);
// }