# Quick Start - Image Upload for Production

## 1️⃣ FOR LOCAL TESTING (Right Now)

Run the app:
```bash
mvn clean spring-boot:run
```

Test upload:
```bash
curl -X POST http://localhost:8080/api/uploads/image \
  -F "file=@test.jpg"
```

Expected response:
```json
{
  "filename": "uuid-here.jpg",
  "url": "http://localhost:8080/uploads/uuid-here.jpg"
}
```

## 2️⃣ FOR RENDER DEPLOYMENT

Set these environment variables in Render Dashboard:

| Variable | Value |
|----------|-------|
| `APP_UPLOADS_DIR` | `/app/uploads` |
| `APP_BASE_URL` | `https://bitdding-backend.onrender.com` |
| `APP_CORS_ALLOWED_ORIGINS` | `https://bitdding-frontend.vercel.app,*` |

Deploy: Push to GitHub → Render auto-deploys

## 3️⃣ VERIFY IN PRODUCTION

```bash
# Should return full URLs
curl https://bitdding-backend.onrender.com/api/products

# Test upload
curl -X POST https://bitdding-backend.onrender.com/api/uploads/image \
  -F "file=@test.jpg"
```

Images will be accessible at:
```
https://bitdding-backend.onrender.com/uploads/<filename>
```

## 4️⃣ FRONTEND (VERCEL) USAGE

```javascript
// Images are now complete URLs in API responses
const products = await fetch('https://bitdding-backend.onrender.com/api/products')
    .then(r => r.json());

// Use imageUrl directly:
products.forEach(product => {
    console.log(product.imageUrl);  // ✅ Already has full URL
});
```

HTML:
```html
<img src={product.imageUrl} alt={product.name} />
```

---

## 📍 Key Changes at a Glance

- ✅ **ProductResponse**: Returns `imageUrl` instead of `imageFilename`
- ✅ **UploadsController**: New `/api/uploads/image` endpoint
- ✅ **AppProperties**: Configurable base URL per environment
- ✅ **application.yml**: Environment variable support
- ✅ **CORS**: Allows access from Vercel frontend
- ✅ **File Upload**: Safe, unique filenames with UUID

---

## 🚀 Deploy Checklist

- [ ] Push code to GitHub
- [ ] Go to Render Dashboard
- [ ] Set the 3 environment variables (see table above)
- [ ] Service auto-deploys
- [ ] Test `/api/uploads/image` endpoint
- [ ] Verify product images show full URLs

That's it! 🎉
