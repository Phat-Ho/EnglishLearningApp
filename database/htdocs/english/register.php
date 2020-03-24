<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once "connect.php";
    $email = $_POST['email'];
    $password = $_POST['password'];

    $checkEmailQuery = "SELECT * FROM users WHERE email = ?";
    $query = "INSERT INTO users (email, password) VALUES ('$email', '$password')";

    $stmt = $conn->prepare($checkEmailQuery);
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $data = $stmt->get_result();

    //Kiểm tra email đã tồn tại hay chưa
    if($row = $data->fetch_assoc()){
        $result["success"] = "false";
        $result["message"] = "email existence";

        echo json_encode($result);
        mysqli_close($conn);
    }

    //Nếu chưa tồn tại thì thực hiện đăng kí
    if(mysqli_query($conn, $query)){
        $result["success"] = "true";
        $result["message"] = "success";

        echo json_encode($result);
        mysqli_close($conn);
    }else{
        $result["success"] = "false";
        $result["message"] = "error";

        echo json_encode($result);
        mysqli_close($conn);
    }
}else{
    $result["success"] = "false";
    $result["message"] = "error";

    echo json_encode($result);
}
?>