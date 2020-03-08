<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once "connect.php";
    $email = $_POST['email'];
    $password = $_POST['password'];

    $query = "INSERT INTO users (email, password) VALUES ('$email', '$password')";

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