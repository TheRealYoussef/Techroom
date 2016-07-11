<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "Name of school", "Country" : "School's Country"};
	//return
	//[{"Status" : 409, "Message" : "error"}] OR [{"Status" : 200, "Teachers" : [{"Name" : "teacher name", "Image" : "teacher image", "Orientation" : orientation}, {"Name" : "teacher name", "Image" : "teacher image", "Orientation" : orientation}, ...]}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	$query = "SELECT ID FROM Country WHERE 
					Name = '$decodedJSON->Country'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;
	$query = "SELECT ID FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$schoolID  = $obj->ID;
	
	$ret = array();
	$mainArr = array("Status" => 200);
	$teachers = array();
	$query = "SELECT Name, Image, ID, Orientation FROM Teacher WHERE School = $schoolID";
	$result = mysqli_query($conn, $query);
	while ($obj = mysqli_fetch_object($result)) {
		$name = $obj->Name;
		$image = "";
		if ($obj->Image == 1) {
			$id = $obj->ID;
			$image = "TeachersUploadedImages/$id.png";
		}
		$orientation = $obj->Orientation;
		$teacher = array("Name" => $name, "Image" => $image, "Orientation" => $orientation);
		array_push($teachers, $teacher);
	}
	mysqli_close($conn);
	$mainArr["Teachers"] = $teachers;
	array_push($ret, $mainArr);
	echo json_encode($ret);
	exit;
?>