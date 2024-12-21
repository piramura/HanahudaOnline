# Mavenを使ってビルドするステージ
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# プロジェクト全体をコピー
COPY . .

# Mavenを使ってプロジェクトをビルド（テストはスキップ）
RUN mvn clean package -DskipTests

# 実行環境用のステージ
FROM openjdk:17-jdk-slim
WORKDIR /app

# ビルド済みのjarファイルをコピー
COPY --from=build /app/target/hanafuda-game-1.0-SNAPSHOT.jar app.jar

# サーバーポートを公開
EXPOSE 10030

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]