Volley Request Manager
======================

 - Easy and reusable interface
 - Possibility to use different queues
 - Background and volley default queues implementations
 - Possibility to create your own queues
 - Factory that will help to create your own queues
 - Callback that handle result in background and deliver result in UI thread
 - Possibility to use default Volley Listeners
 - Load Images with different Image Loaders
 - Factory that will help to create your own Image Loader
 - Possibility to clear Image Loader memory cache

#####Initialize manager :
```java
RequestManager.initializeWith(contex);
```

#####Choose the best way that you need :

```java
//Queue using custom listener
RequestManager.queue()
        .useBackgroundQueue()
        .addRequest(new TestJsonRequest(), mRequestCallback)
        .start();
        
//Queue using default volley Response and Error listener
RequestManager
        .queue()
        .useBackgroundQueue()
        .addRequest(new TestJsonRequest(), mListener, mErrorListener)
        .start();
        
//load image
 RequestManager
            .loader()
            .useDefaultLoader()
            .obtain()
            .get(
                    "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
                    mImageListener
            );
            
//clear chache
 RequestManager
            .loader()
            .useDefaultLoader()
            .clearCache();        
```

#####Custom listener implementation :
```java
private RequestCallback mRequestCallback = new RequestCallback<JSONObject, Void>() {
    @Override
    public Void doInBackground(JSONObject response) {
        //triggers on called thread
        L.i(response.toString());
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
       //always triggers on UI thread
        Toast.makeText(getApplicationContext(), "Toast from UI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(VolleyError error) {
        //works as volleys Response.ErrorListener
        L.e(error.toString());
    }
};
```

#####Request implementation example:
```java
public class TestJsonRequest extends RequestInterface<JSONObject, Void> {

    @Override
    public Request create() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("httpbin.org");
        uri.path("get");
        uri.appendQueryParameter("name", "Jon Doe");
        uri.appendQueryParameter("age", "21");
        String url = uri.build().toString();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                
                //if you want to use Callbacks provided
                //via Request Manager interface
                //use useInterfaceListener() and useInterfaceErrorListener()
                //instead of creating new listenets here
                
                useInterfaceListener(),
                useInterfaceErrorListener());
        return request;
    }
}
```
