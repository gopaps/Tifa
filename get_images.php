<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "dbtes";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    // Return array kosong untuk menjaga struktur array
    echo json_encode([]);
    exit;
}

$sql = "SELECT id, image_name, image_data FROM images";
$result = $conn->query($sql);

$images = array();

if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $images[] = [
            'id' => $row['id'],
            'image_name' => $row['image_name'],
            'image_data' => base64_encode($row['image_data'])
        ];
    }
}

echo json_encode($images); // Langsung return array tanpa wrapper
$conn->close();
?>