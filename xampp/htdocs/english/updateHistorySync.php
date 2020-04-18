<?php
    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        require_once "connect.php";

        $userId = $_GET['userid'];
        $wordId = $_GET['wordid'];
        $updateHistoryQuery = "UPDATE history SET syncStatus=1 WHERE userID=$userId AND wordID=$wordId";
        $data = $conn->query($updateHistoryQuery);

        if($data){
            $result["message"] = "success";

            echo json_encode($result);
        }else{
            $result["message"] = "fail";

            echo json_encode($result);
        }
    }else{
        $result["message"] = "error method";

        echo json_encode($result);
    }
?>