<?php

if($_SERVER['REQUEST_METHOD'] == 'GET'){
    require_once "connect.php";
    $email = $_GET['email'];

    $stmt = $conn->prepare('SELECT * FROM users WHERE email = ?');
    $stmt->bind_param('s', $email);

    $stmt->execute();

    $data = $stmt->get_result();

    if($row = $data->fetch_assoc()){
        $result['userID'] = $row['userID'];
        $result['email'] = $row['email'];

        echo json_encode($result);
    }
}
?>