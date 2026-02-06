# Application Configuration for Render

## Required Environment Variables for Render

Set these in your Render Dashboard under "Environment":

```
# Image uploads directory (Render uses /app as working directory)
APP_UPLOADS_DIR=/app/uploads

# Your backend public URL
APP_BASE_URL=https://bitdding-backend.onrender.com

# Frontend domain for CORS
APP_CORS_ALLOWED_ORIGINS=https://bitdding-frontend.vercel.app,*

# Server port (Render sets this via PORT env var)
# PORT is automatically set by Render, no need to configure
```

## Steps in Render Dashboard

1. Go to your backend service
2. Click "Environment" tab
3. Add the above environment variables
4. Click "Save Changes"
5. Service will auto-redeploy

## Local Testing Before Render Deployment

Create a `.env.local` file (not committed to git):

```properties
APP_UPLOADS_DIR=./uploads
APP_BASE_URL=http://localhost:8080
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173,https://bitdding-frontend.vercel.app
```

Then run:
```bash
export $(cat .env.local | xargs)
mvn spring-boot:run
```

## Verify Configuration on Render

After deployment, test:

```bash
# Should return full image URLs
curl https://bitdding-backend.onrender.com/api/products

# Should accept image uploads
curl -X POST https://bitdding-backend.onrender.com/api/uploads/image \
  -F "file=@test.jpg"
```

Expected response should show:
- `"url": "https://bitdding-backend.onrender.com/uploads/..."`
