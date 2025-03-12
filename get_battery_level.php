<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");  // Untuk development saja

$servername = "localhost";
$username = "root";  // Ganti dengan username MySQL
$password = "";  // Ganti dengan password MySQL
$dbname = "dbtes";  // Ganti dengan nama database

// Buat koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

// Prepare statement untuk keamanan
$stmt = $conn->prepare("SELECT battery_level, timestamp FROM battery_data ORDER BY timestamp DESC LIMIT 1");
$stmt->execute();
$result = $stmt->get_result();

$response = [];

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $response = [
        "battery_level" => $row['battery_level'],
        "timestamp" => $row['timestamp']
    ];
} else {
    $response = [
        "battery_level" => 0,
        "timestamp" => date('Y-m-d H:i:s')
    ];
}

echo json_encode($response);

$stmt->close();
$conn->close();
?>