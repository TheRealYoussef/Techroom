<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"LessonID" : lesson id, "Username" : "student username", "ShoolName" : "school name", "Country" : "school's country", "Anonymous" : 0 or 1, "Question" : "question"}
	//Return [{'Status' : 200}] OR [{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}

	$query = "SELECT ID FROM Country WHERE Name = '$decodedJSON->Country'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;

	$query = "SELECT ID FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$schoolID  = $obj->ID;
	
	$query = "SELECT ID, Name FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No student with username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$studentID = $obj->ID;
	$name = $obj->Name;
	
	$query = "SELECT Teacher FROM Lesson WHERE ID = $decodedJSON->LessonID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Lesson does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$teacherID = $obj->Teacher;
	
	$query = "INSERT INTO Discussion (Question, Answer, Lesson, Student, Visible, Anonymous) VALUES ('$decodedJSON->Question', '', $decodedJSON->LessonID, $studentID, 0, $decodedJSON->Anonymous)";
	mysqli_query($conn, $query);
	$discussionID = mysqli_insert_id($conn);
	
	
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
	
	//Array of device ID's which you want to send the notification to (obtained from database)
	//In this case, we are sending the notification to only one device
	$tokens = array();
	
	$query = "SELECT DeviceID FROM Teacher WHERE ID = $teacherID";
	$result = mysqli_query($conn, $query);
	mysqli_close($conn);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Teacher does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$deviceID = $obj->DeviceID;
	
	array_push($tokens, $deviceID);

	$message = array(
		'ID' => 'AddDiscussion',
		'Message' => "$name asked a question",
		'DiscussionID' => $discussionID
		);
	
	sendNotification($tokens, $message);
	
	echo "[{'Status' : 200}]";
	exit;
?>