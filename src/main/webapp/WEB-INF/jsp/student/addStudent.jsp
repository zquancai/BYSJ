<%--
  Created by IntelliJ IDEA.
  User: jzchen
  Date: 2015/3/15 0015
  Time: 下午 11:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <script src="${pageContext.request.contextPath}/resources/jquery-2.1.3.js"></script>
    <title>添加学生</title>
</head>
<body>
<label>添加学生</label><br/>

<button id="submit" value="提交" >提交</button>

<script>
  $("#submit").click(function(){
    $.ajax({
      method: "POST",
      url: "updateStudent",
      data:{
        jsonObject: JSON.stringify(
                {
                  id: 4,
                  password: "22222",
                  campusId: 1,
                  majorId: 1,
                  collegeId: 1,
                  studentNo: "studentNo",
                  name: "name1",
                  sex: "女",
                  grade: "grade1",
                  cla: "class1",
                  email: "666666"
                }
        )
      },
      success:function(data){
        console.log(data);
      },
      error:function(e){
        console.log(e);
      }
    })
  });
</script>


</body>
</html>
