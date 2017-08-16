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
						parent: "main_toolbarObj",
						iconset: "awesome"
		});
		myToolbar.addButton("refresh", 11, "刷新", "fa fa-refresh", "fa fa-refresh");
		myToolbar.addSeparator("sep4", 12);
		myToolbar.attachEvent("onClick", function(id) { initData();});
		myGrid = new dhtmlXGridObject('main_gridbox');
		myGrid.setImagePath("plugins/dhtmlxSuite_v51_std/codebase/imgs/");
		myGrid.setHeader("序号,机器编号,账号数,阳光数");
		myGrid.setColumnIds("number,phone,count,sun");
		myGrid.setInitWidths("220,220,220,220");
		myGrid.setColAlign("left,left,left,left");
		myGrid.setColTypes("txt,txt,txt,txt");
		myGrid.setColSorting("int,str,int,int");
		myGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
		myGrid.init();
		//myGrid.enableAutoWidth(true);
        initData();
	};
	// ********************************************************************************************************************************************
	// 内部方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	function initData(){
        var data ={total_count:0, pos:0, rows:[]};
        var req = {jsonContent:'{"function":"F100007","user":{"id":"1","session":"123"},"content":{"phone":""}}'};
        $.ajax({
            type: "POST",
            url: "/action/lfs/action/FunctionAction",
            data: req,
            dataType: "json",
            success: function (message) {
                if(message.head.errorNo==""){
                    data.rows = message.list;
                    data.total_count= message.list.length
                    myGrid.parse(data.rows,"js");
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
	function doOnRowDblClicked(rowId){
        //alert(rowId);
        var id= "tab_"+rowId;
        var name = "阳光详情"+rowId ;
        if(parent._myTabbar.tabs(id)){
            parent._myTabbar.tabs(id).setActive();
        }else{
            parent._myTabbar.addTab(id, name, "180px",null,true);
            parent._myTabbar.tabs(id).attachURL("html/sun/details.html?id="+rowId,null);
        }
	}
});
