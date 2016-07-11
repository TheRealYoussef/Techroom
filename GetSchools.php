<?php
	header('Content-Type: application/json');
	//return
	//[{"Status" : 409, "Message" : "error"}] OR [{"Status" : 200, "Schools" : [{"School" : school name", "Country" : "country"}, {"School" : school name", "Country" : "country"}, ...]}]
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	
	$ret = array();
	$mainArr = array("Status" => 200);
	$schools = array();
	$query = "SELECT Name, Country FROM School";
	$result = mysqli_query($conn, $query);
	while ($obj = mysqli_fetch_object($result)) {
		$name = $obj->Name;
		$countryID = $obj->Country;
		$query = "SELECT Name FROM Country WHERE ID = $countryID";
		$result2 = mysqli_query($conn, $query);
		$country =  mysqli_fetch_object($result2)->Name;
		$school = array("School" => $name, "Country" => $country);
		array_push($schools, $school);
	}
	
	mysqli_close($conn);
	$mainArr["Schools"] = $schools;
	array_push($ret, $mainArr);
	echo json_encode($ret);
	exit;
?>