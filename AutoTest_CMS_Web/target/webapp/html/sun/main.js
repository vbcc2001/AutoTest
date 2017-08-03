define(function(require, exports, module) {
	// ********************************************************************************************************************************************
	// 变量区              **************************************************************************************************************************
	//*********************************************************************************************************************************************
	var myToolbar = null;
	var myGrid = null;
	// ********************************************************************************************************************************************
	// 对外方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	/**-------------------------------------form表单初始化------------------------------------*/
	exports.init = function() {
		myToolbar = new dhtmlXToolbarObject({
						parent: "toolbarObj",
						iconset: "awesome"
		});
		myToolbar.addButton("刷新", 11, "Paste", "fa fa-refresh", "fa fa-refresh");
		myToolbar.addSeparator("sep4", 12);
		myGrid = new dhtmlXGridObject('gridbox');
		myGrid.setImagePath("plugins/dhtmlxSuite_v51_std/codebase/imgs/");
		myGrid.setHeader("Sales,Book Title,Author,Price,In Store,Shipping,Bestseller,Date of Publication");
		myGrid.setColumnIds("sales,title,author,price,store,shipping,seller,date");
		myGrid.setInitWidths("80,150,50%,20%,80,80,80,100");
		myGrid.setColAlign("right,left,left,right,center,left,center,center");
		myGrid.setColTypes("dyn,ed,txt,price,ch,coro,ch,ro");
		myGrid.setColSorting("int,str,str,int,str,str,str,date");
		myGrid.init();
		//myGrid.enableAutoWidth(true);
		myGrid.load("html/sun/data.xml");
	};
	// ********************************************************************************************************************************************
	// 内部方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
});
