<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "School Name", "Country" : "Country", Username" : "username entered"}
	//Return
	//[
	//	{
	//		"Status" : 200, 
	//		"Name" : "name", 
	//		"Image" : "image", 
	//		"Orientation" : orientation, 
	//		"Account" : 0 if student or 1 if teacher (this will be 0 because this is a student account),
	//		'Lessons' : 
	//		[
	//			{
	//				'LessonID': lesson id, 
	//				'Title' : 'lesson title', 
	//				'Description' : 'lesson description',
	//				'Teacher' : 'name of teacher'
	//			}, 
	//			{
	//				'LessonID': lesson id, 
	//				'Title' : 'lesson title',
	//				'Description' : 'lesson description', 
	//				'Teacher' : 'name of teacher'
	//			}, 
	//			...
	//		]
	//	}
	//]
	//OR
	//[
	//	{
	//		"Status" : 200, 
	//		"Name" : "name", 
	//		"Image" : "image", 
	//		"Orientation" : orientation, 
	//		"Account" : 0 if student or 1 if teacher (this will be 1 because this is a teacher account), 
	//		'Lessons' : 
	//		[
	//			{
	//				'LessonID': lesson id, 
	//				'Title' : 'lesson title', 
	//				'Description' : 'lesson description',
	//			}, 
	//			{
	//				'LessonID': lesson id, 
	//				'Title' : 'lesson title',
	//				'Description' : 'lesson description', 
	//			}, 
	//			...
	//		]
	//	}
	//]
    //OR 
	//[{"Status" : 409, "Message" : "error"}]
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
	
	$ret = array();
	
	$query = "SELECT Name, Image, Orientation, ID FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		$obj =  mysqli_fetch_object($result);
		$image = "";
		$studentID = $obj->ID;
		if ($obj->Image == 1) {
			$image = "StudentsUploadedImages/$studentID.png";
		}
		$mainObj = array("Status" => 200, "Name" => $obj->Name, "Image" => $image, "Orientation" => $obj->Orientation, "Account" => 0);
		$lessons = array();
		$query = "SELECT Lesson FROM StudentLesson WHERE Student = $studentID";
		$result = mysqli_query($conn, $query);
		while ($obj = mysqli_fetch_object($result)) {
			$lessonID = $obj->Lesson;
			$query = "SELECT Name, Description, Teacher FROM Lesson WHERE ID = $lessonID";
			$result2 =  mysqli_query($conn, $query);
			$obj2 = mysqli_fetch_object($result2);
			$title = $obj2->Name;
			$description = $obj2->Description;
			$teacherID = $obj2->Teacher;
			$query = "SELECT Name FROM Teacher WHERE ID = $teacherID";
			$result2 =  mysqli_query($conn, $query);
			$obj2 = mysqli_fetch_object($result2);
			$teacher = $obj2->Name;
			$lesson = array("LessonID" => $lessonID, "Title" => $title, "Description" => $description, "Teacher" => $teacher);
			array_push($lessons, $lesson);
		}
		mysqli_close($conn);
		$mainObj["Lessons"] = $lessons;
		array_push($ret, $mainObj);
		echo json_encode($ret);
		exit;
	}
	
	$query = "SELECT Name, Image, Orientation, ID FROM Teacher WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		$obj =  mysqli_fetch_object($result);
		$image = "";
		$teacherID = $obj->ID;
		if ($obj->Image == 1) {
			$image = "TeachersUploadedImages/$teacherID.png";
		}
		$mainObj = array("Status" => 200, "Name" => $obj->Name, "Image" => $image, "Orientation" => $obj->Orientation, "Account" => 1);
		$lessons = array();
		$query = "SELECT ID, Name, Description FROM Lesson WHERE Teacher = $teacherID";
		$result = mysqli_query($conn, $query);
		mysqli_close($conn);
		while ($obj = mysqli_fetch_object($result)) {
			$lessonID = $obj->ID;
			$title = $obj->Name;
			$description = $obj->Description;
			$lesson = array("LessonID" => $lessonID, "Title" => $title, "Description" => $description);
			array_push($lessons, $lesson);
		}
		$mainObj["Lessons"] = $lessons;
		array_push($ret, $mainObj);
		echo json_encode($ret);
		exit;
	}
	
	mysqli_close($conn);
	echo "[{'Status' : 409, 'Message' : 'No username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
	exit;
?>