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
		myToolbar.addButton("export", 13, "导出汇总", "fa fa-arrow-up", "fa fa-arrow-up");
        myToolbar.addSeparator("sep4", 14);
        myToolbar.addButton("export_2", 15, "导出明细(可多选)", "fa fa-bar-chart", "fa fa-arrow-up");
		myToolbar.attachEvent("onClick", function(id) {
            if(id=="refresh"){
                initData();
            }
            if(id=="export"){
                var json = encodeURI('{"function":"F100021","user":{"id":"1","session":"123"},"content":{type:"hongbao"}}')
                window.location.href='/action/lfs/action/CsvAction?jsonContent='+json;
            }
            if(id=="export_2"){
                var ids = myGrid.getSelectedRowId();
                if(ids ==null){
                    dhtmlx.confirm({
                        title:"提示",
                        type:"confirm-warning",
                        text:"请选择一行数据!<br>(按住shift或control键可多选)",
                        callback:function(result){ }
                    });
                }else{
                  var json = encodeURI('{"function":"F100022","user":{"id":"1","session":"123"},"content":{type:"hongbao",ids:"'+ids+'"}}')
                  window.location.href='/action/lfs/action/CsvAction?jsonContent='+json;
                }
            }
		});
		myGrid = new dhtmlXGridObject('main_gridbox');
		myGrid.selMultiRows = true;
		myGrid.setImagePath("plugins/dhtmlxSuite_v51_std/codebase/imgs/");
		myGrid.setHeader("序号,机器编号,机器标签,账号数,总豆数");
		myGrid.setColumnIds("number,phone,tag,count,dou");
		myGrid.setInitWidths("40,220,220,160,160");
		myGrid.setColAlign("left,left,left,left,left");
		myGrid.setColTypes("txt,txt,txt,txt,txt");
		myGrid.setColSorting("int,str,str,int,int");
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
        var req = {jsonContent:'{"function":"F100011","user":{"id":"1","session":"123"},"content":{type:"hongbao"}}'};
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
        var id= "tab_account_hongbao_"+rowId;
        var name = "账号-豆-"+rowId.substring(0,6);
        if(parent._myTabbar.tabs(id)){
            parent._myTabbar.tabs(id).setActive();
        }else{
            parent._myTabbar.addTab(id, name, "180px",null,true);
            parent._myTabbar.tabs(id).attachURL("html/accout/hongbao/details.html?id="+rowId,null);
        }
	}
});
