<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"DiscussionID" : discussion id, "Answer" : "answer"}
	//Return [{'Status' : 200}] OR [{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}

	$query = "SELECT Lesson, Visible FROM Discussion WHERE ID = $decodedJSON->DiscussionID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Discussion does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$lessonID = $obj->Lesson;
	$visible = $obj->Visible;
	
	$query = "SELECT Teacher FROM Lesson WHERE ID = $lessonID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Discussion does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$teacherID = $obj->Teacher;
	
	$query = "SELECT Name FROM Teacher WHERE ID = $teacherID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Discussion does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$name = $obj->Name;
	
	$query = "UPDATE Discussion SET Answer = '$decodedJSON->Answer' WHERE ID = $decodedJSON->DiscussionID";
	mysqli_query($conn, $query);
	
	function sendNotification($tokens, $message) {
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			'registration_ids' 	=> $tokens,
			'data'			=> $message
			);
		$headers = array(
			'Authorization: key=AIzaSyAYNGmfAh_hHLda6bMbQFLIVOb8yrKuC6o', //Server key, obtained from firebase account (constant)
			'Content-Type: application/json'
			);
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
		$result = curl_exec($ch);
		curl_close($ch);
	}
	
	if ($visible == 1) {
	
		$tokens = array();
		
		$query = "SELECT Student FROM StudentLesson WHERE Lesson = $lessonID";
		$result = mysqli_query($conn, $query);
		while ($obj = mysqli_fetch_object($result)) {
			$studentID = $obj->Student;
			$query = "SELECT DeviceID FROM Student WHERE ID = $studentID";
			$result2 = mysqli_query($conn, $query);
			$obj = mysqli_fetch_object($result2);
			$deviceID = $obj->DeviceID;
			array_push($tokens, $deviceID);
		}

		$message = array(
			'ID' => 'AnswerDiscussion',
			'Message' => "$name answered a question",
			'DiscussionID' => $decodedJSON->DiscussionID,
			'Answer' => $decodedJSON->Answer
			);
		
		sendNotification($tokens, $message);
	}
	
	echo "[{'Status' : 200}]";
	exit;
?>