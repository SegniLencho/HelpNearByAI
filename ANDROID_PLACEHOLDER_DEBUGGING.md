# Android Placeholder Debugging Guide

## Issue: Placeholders Not Visible on Android

The placeholder visibility issue on Android is typically a **frontend/mobile app problem**, not a backend issue. However, I've created several backend endpoints to help debug and resolve this.

## New API Endpoints for Debugging

### 1. Form Metadata Endpoint
```
GET /api/requests/form-metadata
```

Returns comprehensive form field information including:
- Field placeholders
- Field types (text, textarea, number, select)
- Validation rules
- Required field indicators
- Maximum lengths
- Helpful hints

**Example Response:**
```json
{
  "fields": {
    "title": {
      "placeholder": "Enter a clear title for your request",
      "type": "text",
      "required": true,
      "maxLength": 255,
      "validation": "required|min:5|max:255",
      "hint": "Be specific about what help you need"
    },
    "description": {
      "placeholder": "Describe your request in detail",
      "type": "textarea",
      "required": true,
      "maxLength": 2000,
      "validation": "required|min:10|max:2000",
      "hint": "Include important details like location, timing, and specific requirements"
    }
  },
  "categories": ["Home & Garden", "Transportation", "Technology", ...],
  "urgencyLevels": ["LOW", "MEDIUM", "URGENT"],
  "statusOptions": ["OPEN", "INPROGRESS", "CLOSED"]
}
```

### 2. Debug Endpoints
```
GET /api/debug/ping          # Test connectivity
GET /api/debug/form-test     # Test form field rendering
POST /api/debug/echo         # Test request/response handling
```

## Common Android Placeholder Issues & Solutions

### 1. **Text Color Issues**
**Problem**: Placeholder text color matches background
**Solution**: 
```xml
<!-- In Android XML -->
android:textColorHint="#666666"
android:hint="Your placeholder text"
```

### 2. **Theme/Style Issues**
**Problem**: Material Design theme hiding placeholders
**Solution**:
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:hintEnabled="true"
    app:hintTextColor="@color/hint_color">
    
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter title here" />
</com.google.android.material.textfield.TextInputLayout>
```

### 3. **Dynamic Placeholder Loading**
**Problem**: Placeholders not set when loading from API
**Solution**: Use the form metadata endpoint
```kotlin
// Kotlin example
fun loadFormMetadata() {
    apiService.getFormMetadata().enqueue(object : Callback<RequestFormMetadata> {
        override fun onResponse(call: Call<RequestFormMetadata>, response: Response<RequestFormMetadata>) {
            response.body()?.let { metadata ->
                titleField.hint = metadata.fields["title"]?.placeholder
                descriptionField.hint = metadata.fields["description"]?.placeholder
                // Set other placeholders...
            }
        }
    })
}
```

### 4. **EditText vs TextInputEditText**
**Problem**: Using wrong component type
**Solution**: Use TextInputEditText with TextInputLayout
```xml
<!-- Instead of plain EditText -->
<com.google.android.material.textfield.TextInputLayout>
    <com.google.android.material.textfield.TextInputEditText
        android:hint="Your placeholder" />
</com.google.android.material.textfield.TextInputLayout>
```

### 5. **Dark Mode Issues**
**Problem**: Placeholders invisible in dark mode
**Solution**: Define proper color resources
```xml
<!-- colors.xml -->
<color name="hint_color_light">#666666</color>
<color name="hint_color_dark">#CCCCCC</color>

<!-- In night/colors.xml -->
<color name="hint_color">#CCCCCC</color>
```

## Testing Steps

### 1. Test Backend Connectivity
```bash
curl http://your-server/api/debug/ping
```

### 2. Test Form Metadata
```bash
curl http://your-server/api/requests/form-metadata
```

### 3. Test in Android App
1. Call `/api/debug/form-test` endpoint
2. Verify JSON parsing works correctly
3. Check if placeholders are set programmatically
4. Test on different Android versions/devices

## Request Form Fields

| Field | Type | Required | Placeholder |
|-------|------|----------|-------------|
| title | text | Yes | "Enter a clear title for your request" |
| description | textarea | Yes | "Describe your request in detail" |
| category | select | Yes | "Select a category" |
| reward | number | No | "Enter reward amount (optional)" |
| urgency | select | Yes | "Select urgency level" |
| latitude | number | Yes | "Latitude" |
| longitude | number | Yes | "Longitude" |

## Categories Available
- Home & Garden
- Transportation  
- Technology
- Moving & Delivery
- Pet Care
- Childcare
- Tutoring & Education
- Health & Wellness
- Events & Entertainment
- Professional Services
- Emergency
- Other

## Urgency Levels
- LOW
- MEDIUM  
- URGENT

## Recommended Android Implementation

```kotlin
class CreateRequestActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        
        // Load form metadata from backend
        loadFormMetadata()
    }
    
    private fun loadFormMetadata() {
        apiService.getFormMetadata().enqueue(object : Callback<RequestFormMetadata> {
            override fun onResponse(call: Call<RequestFormMetadata>, response: Response<RequestFormMetadata>) {
                if (response.isSuccessful) {
                    response.body()?.let { setupForm(it) }
                }
            }
            
            override fun onFailure(call: Call<RequestFormMetadata>, t: Throwable) {
                // Fallback to hardcoded placeholders
                setupFallbackPlaceholders()
            }
        })
    }
    
    private fun setupForm(metadata: RequestFormMetadata) {
        metadata.fields["title"]?.let { field ->
            titleInputLayout.hint = field.placeholder
            titleEditText.hint = field.placeholder
        }
        
        metadata.fields["description"]?.let { field ->
            descriptionInputLayout.hint = field.placeholder
            descriptionEditText.hint = field.placeholder
        }
        
        // Setup category spinner
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metadata.categories)
        categorySpinner.adapter = categoryAdapter
        
        // Setup urgency spinner  
        val urgencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metadata.urgencyLevels)
        urgencySpinner.adapter = urgencyAdapter
    }
    
    private fun setupFallbackPlaceholders() {
        titleInputLayout.hint = "Enter a clear title for your request"
        descriptionInputLayout.hint = "Describe your request in detail"
        // Set other fallback placeholders...
    }
}
```

## Next Steps for Android Team

1. **Test API Connectivity**: Use `/api/debug/ping` endpoint
2. **Implement Form Metadata Loading**: Use `/api/requests/form-metadata`
3. **Check UI Components**: Ensure using TextInputLayout + TextInputEditText
4. **Test Color Themes**: Verify placeholder colors in light/dark modes
5. **Test on Multiple Devices**: Different Android versions and screen sizes
6. **Add Fallback Placeholders**: In case API is unavailable

The backend is now providing all necessary form metadata. The placeholder visibility issue is likely in the Android UI implementation or styling.