define(function(require, exports, module) {
	// ********************************************************************************************************************************************
	// 变量区              **************************************************************************************************************************
	//*********************************************************************************************************************************************
	var myToolbar = null;
	var myGrid = null;
	var myDataProcessor = null;
	// ********************************************************************************************************************************************
	// 对外方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	/**-------------------------------------form表单初始化------------------------------------*/
	exports.init = function() {
		myToolbar = new dhtmlXToolbarObject({
						parent: "details_toolbarObj",
						iconset: "awesome"
		});
		myToolbar.addButton("refresh", 11, "刷新", "fa fa-refresh", "fa fa-refresh");
		myToolbar.addSeparator("sep4", 12);
		myToolbar.attachEvent("onClick", function(id) { initData();});
		myGrid = new dhtmlXGridObject('details_gridbox');
		myGrid.setImagePath("plugins/dhtmlxSuite_v51_std/codebase/imgs/");
		myGrid.setHeader("编号,机器编号,机器标签,账号,更新时间,阳光数,阳光更新时间");
		myGrid.setColumnIds("number,phone,tag,accout,update_time,sun,sun_update_time");
		myGrid.setInitWidths("40,220,220,220,220,220,220");
		myGrid.setColAlign("left,left,left,left,left,left,left");
		myGrid.setColTypes("txt,txt,txt,txt,txt,txt,txt");
		myGrid.setColSorting("int,str,str,str,date,int,date");
		myGrid.init();
		//myGrid.enableAutoWidth(true);
        initData(getUrlParam("id"));
	};
	// ********************************************************************************************************************************************
	// 内部方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	function initData(phone){
        var data ={total_count:0, pos:0, data:[]};

        var req = {jsonContent:'{"function":"F100010","user":{"id":"1","session":"123"},"content":{"type":"sun","phone":"'+phone+'"}}'};
        $.ajax({
            type: "POST",
            url: "/action/lfs/action/FunctionAction",
            data: req,
            dataType: "json",
            success: function (message) {
                if(message.head.errorNo==""){
                    data.data = message.list;
                    data.total_count= message.list.length
                    myGrid.parse(data,"js");
                }else{
                    dhtmlx.confirm({
                        title:"数据请求错误！",
                        type:"confirm-warning",
                        text:message.head.errorNo+":"+message.head.errorInfo,
                        callback:function(result){ }
                    });
                }
            },
            error: function (message) {
                dhtmlx.confirm({
                    title:"数据请求错误！",
                    type:"confirm-warning",
                    text:"网络错误！",
                    callback:function(result){ }
                });
            }
        });
	}
    //获取url中的参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }
});
