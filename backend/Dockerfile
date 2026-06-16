# ===== 构建阶段 =====
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 先复制 pom.xml，利用 Docker 层缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# 创建非 root 用户运行应用
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/grade-management-system-1.0.0.jar app.jar

# 更改文件归属
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# JVM 参数：容器内存感知 + GC 优化 + 时区设置
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Duser.timezone=Asia/Shanghai", \
  "-jar", "app.jar"]
