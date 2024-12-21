# JDK 23 の軽量ベースイメージを使用
FROM openjdk:23-jdk

# 作業ディレクトリを設定
WORKDIR /app

# JAR ファイルをコンテナにコピー
COPY hanahuda-online.jar app.jar

# 必要なポートを公開（アプリケーションで使用するポート）
EXPOSE 10030

# JAR ファイルを実行するコマンド
CMD ["java", "-jar", "app.jar"]
