<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"LessonID" : lesson id}
	//return 
	//[
	//	{
	//		"Status" : 200, 
	//		'Discussions' : 
	//		[
	//			{
	//				'Question': "question", 
	//				'Answer' : 'answer', 
	//				'Name' : 'name',
	//				'Visible' : 0 or 1,
	//				'DiscussionID' : id
	//			}, 
	//			{
	//				'Question': "question", 
	//				'Answer' : 'answer', 
	//				'Name' : 'name',
	//				'Visible' : 0 or 1,
	//				'DiscussionID' : id
	//			}, 
	//			...
	//		]
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
	
	$ret = array();
	$mainObj = array("Status" => 200);
	$discussions = array();
	
	$query = "SELECT Question, Answer, Student, Anonymous, Visible, ID FROM Discussion WHERE Lesson = $decodedJSON->LessonID";
	$result = mysqli_query($conn, $query);
	while ($obj = mysqli_fetch_object($result)) {
		$question = $obj->Question;
		$answer = $obj->Answer;
		$studentID = $obj->Student;
		$anonymous = $obj->Anonymous;
		$visible = $obj->Visible;
		$discussionID = $obj->ID;
		$discussion;
		$query = "SELECT Name FROM Student WHERE ID = $studentID";
		$result2 = mysqli_query($conn, $query);
		$obj = mysqli_fetch_object($result2);
		$name = $obj->Name;
		$extension = "";
		if ($anonymous == 1)
			$extension = " (prefers to be anonyomus)";
		$discussion = array("Question" => $question, "Answer" => $answer, "Name" => "Asked by: $name$extension", "Visible" => $visible, "DiscussionID" => $discussionID);
		array_push($discussions, $discussion);
	}
	mysqli_close($conn);
	
	$mainObj["Discussions"] = $discussions;
	array_push($ret, $mainObj);
	echo json_encode($ret);
	exit;
?>