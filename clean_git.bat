@echo off
SETLOCAL EnableDelayedExpansion

echo ===================================================
echo  PEMBERSIH RIWAYAT GIT (HANYA SIMPAN COMMIT TERBARU)
echo ===================================================
echo.
echo Peringatan: Skrip ini akan menghapus SEMUA riwayat commit lama
echo dan hanya menyisakan kondisi file saat ini sebagai commit pertama.
echo.

set /p CONFIRM="Apakah Anda yakin ingin melanjutkan? (Y/N): "
if /i "%CONFIRM%" NEQ "Y" (
    echo Proses dibatalkan.
    goto :EOF
)

set /p BRANCH_NAME="Masukkan nama branch utama Anda (contoh: main atau master): "

echo.
echo [1/6] Membuat branch sementara tanpa riwayat...
git checkout --orphan temp-branch
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo [2/6] Menambahkan semua file...
git add -A
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo [3/6] Membuat commit baru...
git commit -am "Initial commit (Clean history)"
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo [4/6] Menghapus branch utama yang lama di lokal...
git branch -D %BRANCH_NAME%
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo [5/6] Mengubah nama branch saat ini menjadi %BRANCH_NAME%...
git branch -m %BRANCH_NAME%
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo [6/6] Melakukan Force Push ke remote repository...
echo Anda mungkin akan diminta memasukkan kredensial Git.
git push -f origin %BRANCH_NAME%
if %ERRORLEVEL% NEQ 0 goto :ERROR

echo.
echo Mengoptimalkan ruang penyimpanan lokal (Garbage Collection)...
git gc --prune=now

echo.
echo ===================================================
echo Selesai! Riwayat Git Anda berhasil dibersihkan.
echo ===================================================
goto :EOF

:ERROR
echo.
echo [ERROR] Terjadi kesalahan saat mengeksekusi perintah Git.
echo Pastikan skrip ini dijalankan di dalam folder repositori Git Anda.
pause
