var login = function () {
    var name = $("#username").val();
    var pass = $("#pass").val();
    jagg.post("/site/blocks/user/login/ajax/login.jag", { action:"login", username:name, password:pass },
              function (result) {
                  if (!result.error) {
                      location.href = 'index.jag';
                  } else {
                      jagg.message(result.message);
                  }
              }, "json");


};


