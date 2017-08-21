define(function(require, exports, module) {
	// ********************************************************************************************************************************************
	// 变量区              **************************************************************************************************************************
	//*********************************************************************************************************************************************
	var myToolbar = null;
	var myGrid = null;
	var myDataProcessor = null;
	var dhxWins = null;
	var addWindow =null;
	var myForm =null;
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
		myToolbar.addButton("add", 13, "添加", "fa fa-plus", "fa fa-plus");
        myToolbar.addSeparator("sep4", 14);
        myToolbar.addButton("del", 15, "删除", "fa fa-trash", "fa fa-trash");
        myToolbar.addSeparator("sep4", 16);
        myToolbar.addButton("pull", 17, "导出", "fa fa-trash", "fa fa-trash");
        myToolbar.addSeparator("sep4", 18);
		myToolbar.attachEvent("onClick", function(id) {
            if(id=="add"){
                openAddWindow();
            }
            if(id=="refresh"){

                initData();
            }
            if(id=="del"){
                delData();
            }
            if(id=="pull"){
                delData();
            }
		});
		myGrid = new dhtmlXGridObject('main_gridbox');
		myGrid.setImagePath("plugins/dhtmlxSuite_v51_std/codebase/imgs/");
		myGrid.setHeader("序号,机器编号,机器标签,更新日期");
		myGrid.setColumnIds("number,phone,tag,update_time");
		myGrid.setInitWidths("220,220,220,220");
		myGrid.setColAlign("left,left,left,left");
		myGrid.setColTypes("txt,txt,txt,txt");
		myGrid.setColSorting("int,str,str,date");
		myGrid.init();
        initData();
        dhxWins = new dhtmlXWindows();
        dhxWins.attachViewportTo(document.body);
	};
	// ********************************************************************************************************************************************
	// 内部方法            **************************************************************************************************************************
	//*********************************************************************************************************************************************
	function initData(){
        var data ={total_count:0, pos:0, rows:[]};
        var req = {jsonContent:'{"function":"F100002","user":{"id":"1","session":"123"},"content":{}}'};
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
    function openAddWindow(){
        var id = "addWindow";
        var w = 500;
        var h = 260;
        var x = 100;
        var y = 100;
//        if(addWindow == null ){
            addWindow = dhxWins.createWindow(id, x, y, w, h);
            addWindow.setText("添加标签");
//        }else{
//            addWindow.show();
//        }

        var formData = [
            {type: "settings", position: "label-left", labelWidth: 100, inputWidth: 160},
            {type: "fieldset", label: "", inputWidth: 492, list:[
                    {type: "input", offsetLeft: 100,name:"phone" , label: "注册码", value: ""},
                    {type: "input", offsetLeft: 100,name:"tag" , label: "标签", value: ""},
                    {type: "label", label: "",list:[
                                        {type: "button", name:"Submit" ,value: "添加", offsetLeft: 120, offsetTop: 10, inputWidth: 50},
                                        {type: "newcolumn"},
                                        {type: "button", name:"Reset" ,value: "重置", offsetLeft: 8, offsetTop: 10}
                    ]},
            ]}
        ];
        myForm = addWindow.attachForm(formData, true);
        myForm.attachEvent("onButtonClick", function(name){add(name);});
        myForm.attachEvent("onEnter",function(){add("Submit");});
        myForm.setItemFocus("phone");
        myForm = w1.attachForm(formData, true);
    }
    function add(name){
        if(name=="Submit"){
            var phone = $.trim(myForm.getInput("phone").value);
            var tag = $.trim(myForm.getInput("tag").value);
            if(phone!="" && tag!= "" ){
                var req = {jsonContent:'{"function":"F100001","user":{"id":"1","session":"123"},"content":{"phone":"'+phone+'","tag":"'+tag+'","register":""}}'};
                $.ajax({
                    type: "POST",
                    url: "/action/lfs/action/FunctionAction",
                    data: req,
                    dataType: "json",
                    success: function (message) {
                        if(message.head.errorNo==""){
                            dhtmlx.confirm({
                                title:"添加成功！",
                                type:"confirm-warning",
                                text:"添加标签成功",
                                callback:function(result){ }
                            });
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

            }else{
                dhtmlx.confirm({
                    title:"错误提示",
                    type:"confirm-warning",
                    text:"注册码和标签不能为空！",
                    callback:function(result){ }
                });
            };
        }
        if(name=="Reset"){
            var phone = myForm.getInput("phone");
            var tag = myForm.getInput("tag");
            phone.value="";
            tag.value="";
        }
    }

    function delData(){
        var selectedId = myGrid.getSelectedRowId();
        if(selectedId ==null){
            dhtmlx.confirm({
                title:"错误提示",
                type:"confirm-warning",
                text:"请先选择要删除的列！",
                callback:function(result){ }
            });
        }else{
            var req = {jsonContent:'{"function":"F100003","user":{"id":"1","session":"123"},"content":{"id":"'+selectedId+'"}}'};
            $.ajax({
                type: "POST",
                url: "/action/lfs/action/FunctionAction",
                data: req,
                dataType: "json",
                success: function (message) {
                    if(message.head.errorNo==""){
                        dhtmlx.confirm({
                            title:"成功！",
                            type:"confirm-warning",
                            text:"删除标签成功",
                            callback:function(result){ }
                        });
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
    }
});
