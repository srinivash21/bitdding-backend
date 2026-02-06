# Image Upload Configuration & Setup Guide

## Overview
This guide explains the image upload functionality and how to configure it for both local development and production (Render).

---

## Local Development Setup

### 1. Configuration (application.yml)
```yaml
app:
  uploads-dir: ./uploads           # Local directory for storing uploads
  base-url: "http://localhost:8080"  # Local base URL
  cors:
    allowed-origins:
      - "http://localhost:5173"    # Vite frontend
```

### 2. Running Locally
```bash
# The app creates ./uploads directory automatically
mvn clean spring-boot:run

# Test upload:
curl -X POST http://localhost:8080/api/uploads/image \
  -F "file=@test.jpg"
```

Expected response:
```json
{
  "filename": "a1b2c3d4-e5f6-g7h8.jpg",
  "url": "http://localhost:8080/uploads/a1b2c3d4-e5f6-g7h8.jpg"
}
```

---

## Production Setup (Render)

### 1. Configure Environment Variables on Render Dashboard

In Render > Your Service > Environment:

```
APP_UPLOADS_DIR=/app/uploads
APP_BASE_URL=https://bitdding-backend.onrender.com
APP_CORS_ALLOWED_ORIGINS=https://bitdding-frontend.vercel.app,*
```

### 2. Update application.yml for Production

```yaml
app:
  uploads-dir: ${APP_UPLOADS_DIR:/app/uploads}
  base-url: ${APP_BASE_URL:}
  cors:
    allowed-origins:
      - ${APP_CORS_ALLOWED_ORIGINS:https://bitdding-frontend.vercel.app,*}
```

### 3. Render Dockerfile (if needed)

```dockerfile
FROM maven:3.9.0-eclipse-temurin-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

---

## Endpoints

### 1. Upload Single Image
**POST** `/api/uploads/image`
```bash
curl -X POST https://bitdding-backend.onrender.com/api/uploads/image \
  -F "file=@product.jpg"
```

**Response:**
```json
{
  "filename": "uuid-here.jpg",
  "url": "https://bitdding-backend.onrender.com/uploads/uuid-here.jpg"
}
```

### 2. Create Product (with image)
**POST** `/api/products`
```bash
curl -X POST https://bitdding-backend.onrender.com/api/products \
  -F "sellerName=JohnDoe" \
  -F "name=Vintage Watch" \
  -F "description=Beautiful antique watch" \
  -F "startingPrice=50.00" \
  -F "endTime=2026-02-28T18:00:00" \
  -F "image=@watch.jpg"
```

**Response includes full image URL:**
```json
{
  "id": 1,
  "sellerName": "JohnDoe",
  "name": "Vintage Watch",
  "imageUrl": "https://bitdding-backend.onrender.com/uploads/uuid-here.jpg",
  "startingPrice": "50.00",
  "currentPrice": "50.00",
  "status": "ACTIVE",
  "endTime": "2026-02-28T18:00:00"
}
```

### 3. Get All Products
**GET** `/api/products`
```bash
curl https://bitdding-backend.onrender.com/api/products
```

All responses include full `imageUrl`.

### 4. Get Product by ID
**GET** `/api/products/{id}`
```bash
curl https://bitdding-backend.onrender.com/api/products/1
```

---

## Frontend (Vercel) Integration

### 1. Upload and Create Product
```javascript
const form = new FormData();
form.append('sellerName', 'JohnDoe');
form.append('name', 'Product Name');
form.append('description', 'Description');
form.append('startingPrice', '50.00');
form.append('endTime', '2026-02-28T18:00:00');
form.append('image', fileInput.files[0]);

const response = await fetch('https://bitdding-backend.onrender.com/api/products', {
  method: 'POST',
  body: form,
  credentials: 'include'
});

const product = await response.json();
const imageUrl = product.imageUrl;  // Use this to display the image
```

### 2. Display Images
```html
<!-- Images are served from /uploads/** path -->
<img src="https://bitdding-backend.onrender.com/uploads/uuid-filename.jpg" alt="Product" />
```

---

## Security & Best Practices

✅ **Implemented:**
- Unique filenames using UUID (prevents overwrites)
- File type validation (only JPG/PNG)
- File size limits (10MB max)
- Path normalization (prevents directory traversal)
- CORS configured for frontend domain
- Safe file deletion on product update/delete

⚙️ **Configuration Checklist:**
- [ ] Set `APP_BASE_URL` to your Render URL
- [ ] Set `APP_UPLOADS_DIR` to `/app/uploads`
- [ ] Add frontend Vercel URL to CORS origins
- [ ] Use `https://` URLs for production
- [ ] Ensure `/uploads` directory is writable

---

## Troubleshooting

**Issue: Images not loading in production**
- Check `APP_BASE_URL` is set correctly
- Verify CORS origin matches your frontend domain
- Check that uploads directory has write permissions

**Issue: "Failed to save image"**
- Increase `spring.servlet.multipart.max-file-size` if file > 10MB
- Ensure `/app/uploads` directory exists and is writable

**Issue: Same filename appears multiple times**
- UUIDs ensure unique names. This shouldn't happen.
- Check database for duplicate image entries.

---

## Configuration Reference

| Property | Local | Production |
|----------|-------|-----------|
| `app.uploads-dir` | `./uploads` | `/app/uploads` |
| `app.base-url` | `http://localhost:8080` | `https://bitdding-backend.onrender.com` |
| `spring.servlet.multipart.max-file-size` | 10MB | 10MB |

---

## Additional Notes

- Images are stored on the server filesystem
- For production persistence, consider using a persistent volume or cloud storage (S3) for future upgrades
- All incoming requests are CORS-enabled for safe cross-origin access
- Image URLs are constructed dynamically based on configuration
