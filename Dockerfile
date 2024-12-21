# JDK 23 の軽量ベースイメージを使用
FROM eclipse-temurin:17-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# JAR ファイルをコンテナにコピー
COPY hanahudaonline.jar app.jar

# 必要なポートを公開（アプリケーションで使用するポート）
EXPOSE 10030

# JAR ファイルを実行するコマンド
CMD ["java", "-jar", "app.jar"]
