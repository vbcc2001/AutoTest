define(function(require, exports, module) {
	// ********************************************************************************************************************************************
	// 变量区                **************************************************************************************************************************
	//*********************************************************************************************************************************************
	var myTabbar = null;
	var tree = null;
	// ********************************************************************************************************************************************
	// 对外方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	/**-------------------------------------form表单初始化------------------------------------*/
	exports.init = function(user_name,my_page_id) {
		createMainWindow(user_name);//创建主窗口
		//createTabbar();//创建标签
		//createTree(my_page_id);//创建目录

	};
	exports.addTabbar = function(id,name,url) {
		if(myTabbar.tabs(id)){
			myTabbar.tabs(id).setActive();
		}else{
			myTabbar.addTab(id, name, "120px",null,true);
			myTabbar.tabs(id).attachURL(url);
		}	
	};
	// ********************************************************************************************************************************************
	// 内部方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	/**-------------------------------------创建主窗口---------------------------------------*/
	function createMainWindow(user_name){
		var dhxLayout=new dhtmlXLayoutObject(document.body,"2U");
		var str  ="<div style='float: left;' >当前：<span style='color: #4B08D2;' >"+user_name+"</span></div>"
					+"<div  style='float:right; '>"+
							"<a id='user_quit' href='javascript:void();'>" +
								"<img src='img/logout.png' style='width: 18px;height: 18px;vertical-align:middle' alt='登出' />" +
							"</a>"
					+"</div>";
		dhxLayout.cells("a").setText(str);
		$("#user_quit").on("click",function(){ //绑定退出按钮
	        dhtmlx.confirm({
	            title:"警告！",
	            type:"confirm-warning",
	            text:"确定要退出当前系统？",
	            callback:function(result){ if(result){ window.location.href='/admin/action/web/action/LoginAction?function=Quit'; }}
	        });
		});
		dhxLayout.cells("b").setText("主窗口");
		dhxLayout.cells("a").setWidth("200");
		dhxLayout.cells("a").attachObject("menu_tree");
		dhxLayout.cells("b").attachObject("tab_bar");
		dhxLayout.cells("b").hideHeader();
	};
	/**-------------------------------------创建目录树---------------------------------------*/
	function createTree(my_page_id){
		tree = new dhtmlXTreeObject("menu_tree", "100%", "100%", 0);
		tree.setSkin('dhx_skyblue');
		tree.setImagePath("/admin/front/dhtmlxSuite_4/codebase/imgs/dhxtree_skyblue/");
		tree.enableHighlighting(true);
		tree.loadJSON("/admin/action/web/action/MainAction?function=getMenu");
		tree.setOnClickHandler(open_url);
		tree.setOnLoadingEnd(function(){  tree.selectItem(my_page_id,true,false);  });
	};
	/**------------------------------------创建导航标签-------------------------------------*/
	function createTabbar () {	
		myTabbar = new dhtmlXTabBar("tab_bar");
		myTabbar.enableTabCloseButton(true);
		myTabbar.enableAutoReSize(true);
	};	
	function open_url(item_id){
		var url = tree.getUserData(item_id,"url");
		var id= "tab_"+item_id;
		var name =tree.getItemText(item_id) ;
		if(url==""){
			if(tree.getOpenState(item_id)==1){ tree.closeAllItems(item_id); }
			if(tree.getOpenState(item_id)==-1){ tree.openItem(item_id) ; }
		}else{
			if(myTabbar.tabs(id)){
				myTabbar.tabs(id).setActive();
			}else{
				myTabbar.addTab(id, name, "120px",null,true);
				myTabbar.tabs(id).attachURL(url);
			}	
		}
	}
});