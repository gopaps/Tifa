<?php
$servername = "localhost";
$username = "root";  // Ganti dengan username MySQL
$password = "";  // Ganti dengan password MySQL
$dbname = "dbtes";  // Ganti dengan nama database

// Buat koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Prepare statement untuk mencegah SQL injection
$stmt = $conn->prepare("INSERT INTO battery_data (battery_level) VALUES (?)");
$stmt->bind_param("i", $battery_level);

echo "Starting dummy data generation...\n";

while(true) {
    // Generate random battery level antara 0-100
    $battery_level = rand(0, 100);
    
    // Eksekusi query
    if($stmt->execute()) {
        echo "[".date('Y-m-d H:i:s')."] Data terkirim: $battery_level%\n";
    } else {
        echo "Error: " . $stmt->error . "\n";
    }
    
    // Tunggu 5 detik
    sleep(5);
}

$stmt->close();
$conn->close();
?>