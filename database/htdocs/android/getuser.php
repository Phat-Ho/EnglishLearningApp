<?php

if($_SERVER['REQUEST_METHOD'] == 'GET'){
    require_once "connect.php";
    $email = $_GET['email'];
    $password = $_GET['password'];

    $stmt = $conn->prepare('SELECT * FROM users WHERE email = ? AND password = ? ');
    $stmt->bind_param('ss', $email, $password);

    $stmt->execute();

    $data = $stmt->get_result();

    if($row = $data->fetch_assoc()){
        $result['id'] = $row['userID'];
        $result['email'] = $row['email'];
        $result['password'] = $row['password'];

        echo json_encode($result);
    }
}
?>