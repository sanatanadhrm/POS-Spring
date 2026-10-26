# ==========================================
# STAGE 1: Builder (Kompilasi & Packaging)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# 1. Copy wrapper dan pom.xml untuk memanfaatkan Docker Cache Layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Berikan izin eksekusi pada wrapper Maven
RUN chmod +x mvnw

# Unduh dependensi (akan di-cache selama pom.xml tidak berubah)
RUN ./mvnw dependency:go-offline -B

# 2. Copy source code dan lakukan build jar
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ==========================================
# STAGE 2: Runtime (Image Produksi Minimal)
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# 3. Security: Buat non-root user 'spring'
RUN addgroup -S spring && adduser -S spring -G spring

# 4. Ambil file JAR hasil kompilasi dari STAGE 1
COPY --from=builder /app/target/*.jar app.jar

# Ubah kepemilikan file ke user spring
RUN chown spring:spring app.jar

# 5. Pindah ke non-root user
USER spring:spring

# Port default product-service (8082)
EXPOSE 8082

# 6. Jalankan aplikasi dengan JVM Container Memory Optimization
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]