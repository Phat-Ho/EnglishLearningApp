<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once "connect.php";
    $email = $_POST['email'];
    $password = $_POST['password'];

    $stmt = $conn->prepare('SELECT * FROM users WHERE email = ? AND password = ? ');
    $stmt->bind_param('ss', $email, $password);

    $stmt->execute();

    $data = $stmt->get_result();

    if($row = $data->fetch_assoc()){
        $result['success'] = "true";
        $result['userID'] = $row['userID'];
        $result['email'] = $row['email'];
        $result['password'] = $row['password'];

        echo json_encode($result);
        $conn->close();
    }else{
        $result['success'] = "false";
        
        echo json_encode($result);
        $conn->close();
    }
}
?>