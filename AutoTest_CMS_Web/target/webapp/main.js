define(function(require, exports, module) {
	// ********************************************************************************************************************************************
	// 变量区                **************************************************************************************************************************
	//*********************************************************************************************************************************************

	var dhxLayout =null;
	var tree = null;
	var myTabbar = null;
	// ********************************************************************************************************************************************
	// 对外方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	/**-------------------------------------form表单初始化------------------------------------*/
	exports.init = function() {

	    _is_login = Cookies.get('_is_login');
        if(_is_login){
            createMainWindow("花椒");//创建主窗口
            createTree();//创建目录
            createTabbar();//创建标签
        }else{
            window.location.href="/html/login/login.html";
        }
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
		dhxLayout=new dhtmlXLayoutObject(document.body,"2U");
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
	            callback:function(result){ if(result){
	                //window.location.href='/admin/action/web/action/LoginAction?function=Quit';
	                _is_login = false;
	                Cookies.remove('_is_login');
	                window.location.href='/';
	                }}
	        });
		});

		dhxLayout.cells("a").setWidth("200");
		//dhxLayout.cells("a").attachObject("menu_tree");
		//dhxLayout.cells("b").attachObject("tab_bar");
		dhxLayout.cells("b").setText("主窗口");
		dhxLayout.cells("b").hideHeader();
	};
	/**-------------------------------------创建目录树---------------------------------------*/
	function createTree(){
		var item = [
//            {id: 1, text: "汇总", open: 1, items: [
//                {id: 101, text: "花椒豆",userdata: {url: "html/dou/main.html"}},
//                {id: 102, text: "阳光",userdata: {url: "html/sun/main.html"}}
//            ]},
            {id: 2, text: "账号信息", open: 1, items: [
				{id: 201, text: "阳光账号",userdata: {url: "html/accout/sun/main.html"}},
				{id: 202, text: "红包账号",userdata: {url: "html/accout/hongbao/main.html"}},
                {id: 203, text: "手机标签",userdata: {url: "html/register/main.html"}}
            ]},
            {id: 3, text: "更新注册", open: 1, items: [
                {id: 301, text: "软件更新",userdata: {url: "html/update/main.html"}}
            ]}
		];
		tree = dhxLayout.cells("a").attachTreeView({
			//parent:         "a",  // id/object, container for treeview
			//skin:           "dhx_terrace",  // string, optional, treeview's skin
			iconset:        "font_awesome", // string, optional, sets the font-awesome icons
			multiselect:    true,           // boolean, optional, enables multiselect
			//checkboxes:     true,           // boolean, optional, enables checkboxes
			dnd:            true,           // boolean, optional, enables drag-and-drop
			context_menu:   true,           // boolean, optional, enables context menu
			//json:           "filename.json",// string, optional, json file with struct
			items:          item,             // array, optional, array with tree struct
			onload:         function(){}    // callable, optional, callback for load
		});
		tree.attachEvent("onClick", open_url);
	};
	/**------------------------------------创建导航标签-------------------------------------*/
	function createTabbar () {	
		myTabbar = dhxLayout.cells("b").attachTabbar({
				align: "left",
				mode: "top"
		});
		myTabbar.enableTabCloseButton(true);
		myTabbar.enableAutoReSize(true);
		_myTabbar = myTabbar
	};	
  /**------------------------------------目录内容的点击处理-------------------------------------*/
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
				myTabbar.tabs(id).attachURL(url,null);
			}	
		}
	}
});