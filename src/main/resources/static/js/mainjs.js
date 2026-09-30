$(document).ready(function() {
    
    // Switch between Login and Register tabs
    function switchToLogin() {
        $('#tab-login-btn').addClass('active');
        $('#tab-register-btn').removeClass('active');
        $('#box-login').removeClass('d-none');
        $('#box-register').addClass('d-none');
        $('#auth-alert').addClass('d-none');
    }

    function switchToRegister() {
        $('#tab-register-btn').addClass('active');
        $('#tab-login-btn').removeClass('active');
        $('#box-register').removeClass('d-none');
        $('#box-login').addClass('d-none');
        $('#auth-alert').addClass('d-none');
    }

    $('#tab-login-btn, #link-to-login').click(function(e) {
        e.preventDefault();
        switchToLogin();
    });

    $('#tab-register-btn, #link-to-register').click(function(e) {
        e.preventDefault();
        switchToRegister();
    });

    function showAlert(message, type) {
        $('#auth-alert')
            .removeClass('d-none alert-success alert-danger')
            .addClass('alert-' + type)
            .html(message);
    }

    // Hiển thị thông tin người dùng đăng nhập thành công (khi ở trang profile)
    if ($('#profile').length > 0) {
        $.ajax({
            type: 'GET',
            url: '/users/me',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            beforeSend: function (xhr) {
                if (localStorage.token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                }
            },
            success: function(data) {
                var json = JSON.stringify(data, null, 4);
                // $('#profile').html(json);
                $('#profile').html(data.fullName);
                if (data.images && data.images.trim() !== "" && !data.images.includes("pravatar")) {
                    document.getElementById("images").src = data.images;
                } else {
                    document.getElementById("images").src = "https://cdn-icons-png.flaticon.com/512/847/847969.png";
                }
            },
            error: function(e) {
                var json = e.responseText;
                $('#feedback').html(json);
                alert("Sorry, you are not logged in.");
                window.location.href = "/login";
            }
        });
    }

    // Xử lý Login
    $('#login').click(function(e) {
        e.preventDefault();
        var email = $('#email').val().trim();
        var password = $('#password').val();

        if (!email || !password) {
            showAlert("Vui lòng nhập đầy đủ Email/Username và Mật khẩu!", "danger");
            return;
        }

        var basicInfo = JSON.stringify({
            email: email,
            username: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/login",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: basicInfo,
            success: function (data) {
                localStorage.token = data.token;
                showAlert("Đăng nhập thành công! Đang chuyển hướng...", "success");
                setTimeout(function() {
                    window.location.href = "/user/profile";
                }, 500);
            },
            error: function() {
                showAlert("Đăng nhập thất bại! Sai Email/Username hoặc Mật khẩu.", "danger");
            }
        });
    });

    // Xử lý Register (Đăng ký)
    $('#register').click(function(e) {
        e.preventDefault();
        var fullName = $('#reg_fullName').val().trim();
        var username = $('#reg_username').val().trim();
        var email = $('#reg_email').val().trim();
        var password = $('#reg_password').val();

        if (!fullName || !email || !password) {
            showAlert("Vui lòng điền đầy đủ Họ tên, Email và Mật khẩu!", "danger");
            return;
        }

        var registerData = JSON.stringify({
            fullName: fullName,
            username: username,
            email: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/signup",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: registerData,
            success: function (data) {
                showAlert("Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.", "success");
                $('#email').val(data.username || data.email);
                $('#password').val('');
                setTimeout(function() {
                    switchToLogin();
                }, 1000);
            },
            error: function(e) {
                showAlert("Đăng ký thất bại! Email hoặc Username có thể đã được sử dụng.", "danger");
            }
        });
    });

    // Hàm đăng xuất
    $('#logout').click(function() {
        localStorage.clear();
        window.location.href = "/login";
    });
});
