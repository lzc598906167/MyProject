<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/7/14
  Time: 14:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <meta HTTP-EQUIV="pragma" CONTENT="no-cache">
    <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
    <meta HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="植物信息管理">
    <meta name="keywords" content="植物检索;植物信息管理">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    <title>植物信息管理</title>
    <script src="${rootPath}/js/jquery.min.js"></script>
    <script src="${rootPath}/js/bootstrap.min.js"></script>
    <script>

        $(function () {
            $(".meun-item").click(function () {
                $(".meun-item").removeClass("meun-item-active");
                $(this).addClass("meun-item-active");
                var itmeObj = $(".meun-item").find("img");
                itmeObj.each(function () {
                    var items = $(this).attr("src");
                    items = items.replace("_grey.png", ".png");
                    items = items.replace(".png", "_grey.png")
                    $(this).attr("src", items);
                });
                var attrObj = $(this).find("img").attr("src");
                ;
                attrObj = attrObj.replace("_grey.png", ".png");
                $(this).find("img").attr("src", attrObj);
            });
            $("#topAD").click(function () {
                $("#topA").toggleClass(" glyphicon-triangle-right");
                $("#topA").toggleClass(" glyphicon-triangle-bottom");
            });
            $("#topBD").click(function () {
                $("#topB").toggleClass(" glyphicon-triangle-right");
                $("#topB").toggleClass(" glyphicon-triangle-bottom");
            });
            $("#topCD").click(function () {
                $("#topC").toggleClass(" glyphicon-triangle-right");
                $("#topC").toggleClass(" glyphicon-triangle-bottom");
            });
            $(".toggle-btn").click(function () {
                $("#leftMeun").toggleClass("show");
                $("#rightContent").toggleClass("pd0px");
            });
        })
    </script>
    <script src="${rootPath}/js/jquery.dataTables.min.js"></script>
    <script src="${rootPath}/js/dataTables.bootstrap.js"></script>
    <!--[if lt IE 9]>
    <script src="${rootPath}/js/html5shiv.min.js"></script>
    <script src="${rootPath}/js/respond.min.js"></script>
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="dataTables.bootstrap.css">
    <link href="${rootPath}/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/common.css"/>
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/slide.css"/>
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/jquery.nouislider.css">
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/table.css">
</head>

<body>
<div id="wrap">
    <!-- 左侧菜单栏目块 -->
    <div class="leftMeun" id="leftMeun">
        <div id="logoDiv">
            <p id="logoP"><img id="logo" alt="植物信息管理" src="${rootPath}/images/logo.png"><span>植物信息管理</span></p>
        </div>
        <div id="personInfor">
            <%  String username = session.getAttribute("username").toString(); %>
            <p id="userName">管理员：<%=username%></p>
            <p>
                <a href="${rootPath}/user/user_quit">退出登录</a>
            </p>
        </div>
        <div class="meun-title">植物管理</div>
        <div class="meun-item" href="#page1" aria-controls="page1" role="tab"
             data-toggle="tab" id="1page">
            <img src="${rootPath}/images/icon_plant_grey.png">植物信息管理
        </div>
        <div class="meun-item"+ href="#page2" aria-controls="page2" role="tab"
             data-toggle="tab" id="2page">
            <img src="${rootPath}/images/icon_plantdistri_grey.png">植物分布管理
        </div>
        <div class="meun-item" href="#page3" aria-controls="page3" role="tab"
             data-toggle="tab" id="3page">
            <img src="${rootPath}/images/icon_image_grey.png">植物图片管理
        </div>

        <div class="meun-title">地区管理</div>
        <div class="meun-item" href="#page4" aria-controls="page4"
             role="tab" data-toggle="tab" id="4page">
            <img src="${rootPath}/images/icon_house_grey.png">区域信息管理
        </div>
        <div class="meun-item" href="#page6" aria-controls="page6" role="tab"
             data-toggle="tab" id="6page">
            <img src="${rootPath}/images/icon_quit_grey.png">校区管理
        </div>

        <div class="meun-item" href="#page5" aria-controls="page5" role="tab"
             data-toggle="tab" id="5page">
            <img src="${rootPath}/images/icon_card_grey.png">管理注册请求
        </div>
        <script>
            var userType = "<%=session.getAttribute("name").toString()%>";
            if(userType!="administrator"){
                $("#5page").remove();
            }
        </script>
    </div>
    <!-- 右侧具体内容栏目 -->
    <div id="rightContent">
        <a class="toggle-btn" id="nimei">
            <i class="glyphicon glyphicon-align-justify"></i>
        </a>
        <!-- Tab panes -->
        <div class="tab-content">
            <!-- 植物信息模块 -->
            <div role="tabpanel" class="tab-pane" id="page1">
                <s:action name="plant_list" namespace="/plant" executeResult="true"/>
            </div>

            <!-- 植物分布管理 -->
            <div role="tabpanel" class="tab-pane" id="page2">
                <s:action name="plantdistri_list" namespace="/plantdistri" executeResult="true"/>
            </div>

            <!-- 图片管理模块 -->
            <div role="tabpanel" class="tab-pane" id="page3">
                <s:action name="image_list" namespace="/image" executeResult="true"/>
            </div>

            <!--区域管理模块-->
            <div role="tabpanel" class="tab-pane" id="page4">
                <s:action name="section_list" namespace="/section" executeResult="true"/>
            </div>

            <!--校区管理-->
            <div role="tabpanel" class="tab-pane" id="page6">
                <s:action name="compus_list" namespace="/compus" executeResult="true"/>
            </div>
            <!--管理注册请求-->
            <div role="tabpanel" class="tab-pane" id="page5">
                <s:action name="user_getRegistUsers" namespace="/user" executeResult="true"/>
            </div>
            <script>
                (function ($) {
                    $.getUrlParam = function (name) {
                        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                        var r = window.location.search.substr(1).match(reg);
                        if (r != null)
                            return unescape(r[2]);
                        return null;
                    }
                })(jQuery);
                var active = $.getUrlParam('activeNo');
                if(active == null){
                    active = 1;
                }
                var div_class = $("#page"+active);
                var div_class2 = $("#"+active+"page")
                div_class.addClass("active");
                div_class2.addClass("meun-item-active");
            </script>
        </div>
    </div>
</div>
</body>

</html>
</html>
