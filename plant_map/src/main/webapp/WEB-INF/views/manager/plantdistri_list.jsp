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
<head lang="en">
    <title>植物分布管理</title>
</head>
<body>
<div class="check-div form-inline">
    <div class="col-xs-5">
        <input type="text" class=" form-control input-sm" placeholder="输入文字搜索"
               style="height: 40px!important;">
        <button class="btn btn-white btn-xs ">查 询</button>
    </div>
    <div class="col-xs-4 col-lg-4  col-md-5"
         style="padding-right: 40px;text-align: right;float: right;">
    </div>

</div>
<div class="data-div" style="width: 97%;margin:0 auto;">
    <div class="table-responsive">
        <table class="table table-striped table-condensed table-hover" style="table-layout:fixed;"
               id="table1">
            <caption style="text-align:center;font-size: 25px;margin-bottom: 10px;">
                植物分布列表
                <a class="btn btn-yellow btn-xs" data-toggle="modal"
                   href="plantdistri/plantdistri_add" data-target="#addPlantdistri" style="position: relative;
                                        font-size: 18px;float: right;bottom: -20px;right: 20px;">添加信息
                </a>
            </caption>
            <thead>
            <tr>
                <th>编号</th>
                <th>植物编号</th>
                <th>区域编号</th>
                <th>经度</th>
                <th>纬度</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="plantdistris" var="plantdistri" status="status">
                <tr>
                    <td>${plantdistri.id}</td>
                    <td>${plantdistri.plantId}</td>
                    <td>${plantdistri.sectionId}</td>
                    <td>${plantdistri.lat}</td>
                    <td>${plantdistri.log}</td>
                    <td>
                        <a href="plantdistri/plantdistri_edit?plantdistri.id=${plantdistri.id}" data-toggle="modal"
                           class="btn btn-green btn-xs"
                           data-target="#editPlantdistri${status.count}" id="${status.count+200}">编辑</a>
                        <a class="btn btn-danger btn-xs" data-toggle="modal"
                           data-target="#deletePlantdistri${status.count}">删除
                        </a>
                    </td>
                </tr>
                <!--弹出模态窗口 编辑植物坐标-->
                <div class="modal fade" id="editPlantdistri${status.count}" role="dialog" tabindex="-1">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                        </div>
                    </div>
                </div>
                <!--弹出删除警告窗口-->
                <div class="modal fade" id="deletePlantdistri${status.count}" role="dialog" aria-labelledby="gridSystemModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                        aria-hidden="true">&times;</span></button>
                                <h4 class="modal-title" id="gridSystemModalLabel">提示</h4>
                            </div>
                            <div class="modal-body">
                                <div class="container-fluid">
                                    确定要删除该植物坐标？删除后不可恢复！
                                </div>
                            </div>
                            <form class="form-horizontal" action="plantdistri/plantdistri_delete">
                                <div class="modal-footer">
                                    <input type="hidden" value="${plantdistri.id}" name="plantdistri.id"/>
                                    <button type="button" class="btn btn-xs btn-white" data-dismiss="modal">取 消</button>
                                    <button type="submit" class="btn btn-xs btn-danger">确 定</button>
                                </div>
                            </form>
                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal-dialog -->
                </div>
                <!-- /.modal -->
            </s:iterator>
            </tbody>
        </table>
    </div>

</div>
<!--页码块-->
<footer class="footer">
    <ul class="pagination">
        <li>
            <select>
                <option>1</option>
                <option>2</option>
                <option>3</option>
                <option>4</option>
                <option>5</option>
                <option>6</option>
                <option>7</option>
                <option>8</option>
                <option>9</option>
                <option>10</option>
            </select>
            页
        </li>
        <li class="gray">
            共20页
        </li>
        <li>
            <a href="">上一页</a>
        </li>
        <li>
            <a href="">下一页</a>
        </li>
    </ul>
</footer>


<!--弹出模态窗口 添加植物坐标-->
<div class="modal fade" id="addPlantdistri" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
        </div>
    </div>
</div>



</body>
</html>
