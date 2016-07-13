<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"DiscussionID" : discussion id}
	//return 
	//[
	//	{
	//		'Status' : 200,
	//		'Question' : 'question',
	//		'Answer' : 'answer',
	//		'Name' : 'name'
	//	}
	//]
	//OR
	//[{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}

	
	$query = "SELECT Question, Answer, Student, Anonymous FROM Discussion WHERE ID = $decodedJSON->DiscussionID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Discussion does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$question = $obj->Question;
	$answer = $obj->Answer;
	$studentID = $obj->Student;
	$anonymous = $obj->Anonymous;
	$query = "SELECT Name FROM Student WHERE ID = $studentID";
	$result = mysqli_query($conn, $query);
	$obj = mysqli_fetch_object($result);
	$name = 'Asked by: $obj->Name';
	if ($anonymous == 1)
		$name = "Asker is anonymous";
	mysqli_close($conn);
	echo "[{'Status' : 200, 'Question' : '$question', 'Answer' : '$answer', 'Name' : '$name'}]";
	exit;
?>