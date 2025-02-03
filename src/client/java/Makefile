.PHONY: set-display run

# DISPLAY環境変数をエクスポートして実行
set-display:
	@export DISPLAY=192.168.1.39:0 && echo "DISPLAY set to $$DISPLAY"

# DISPLAYを使ってアプリケーションを起動する場合
run:
	@export DISPLAY=192.168.1.39:0 && javac *.java && java Hanahuda

# UECでアプリケーションを起動する場合
UECrun:
	@javac *.java && java Hanahuda