<?php
    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        require_once "connect.php";
        $wordID = $_GET['wordid'];
        $userID = $_GET['userid'];
        $datetime = $_GET['datetime'];

        $addHistoryQuery = "INSERT INTO history (userID, wordID, date) VALUES ('$userID', '$wordID', '$datetime')";

        if(mysqli_query($conn, $addHistoryQuery)){
            $result["message"] = "success";

            echo json_encode($result);
            mysqli_close($conn);
        }else{
            $result["message"] = "error";

            echo json_encode($result);
            mysqli_close($conn);
        }
    }else{
        $result["message"] = "error method";

        echo json_encode($result);
    }
?>