Volley Request Manager
======================

- One centralized and reusable model
- Possibility to use different queues
- Background and volley default queues implementations
- Possibility to create your own queues
- Factory that will help to create your own queues
- Callback that handle result in background and deliver result in UI thread
- Possibility to use default Volley Listeners
- More of sweet features come soon

#####Initialize manager :
```java
RequestManager.initializeWith(contex);
```

#####Choose the best way that you need :

```java
//Queue using default volley Response and Error listener
RequestManager
       .queue()
        .usingBackgroundQueue()
        .addRequest(new TestRequest(mListener, mErrorListener))
        .start();
        
  
//Queue using custom listener
RequestManager.queue()
        .usingBackgroundQueue()
        .addRequest(new TestJsonRequest(mRequestCallback))
        .start();    
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
public class TestJsonRequest extends RequestInterface<Object, JSONObject, Void> {

    public TestJsonRequest(RequestCallback<JSONObject, Void> requestCallback) {
        super(requestCallback);
    }

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
                this,
                this);

        return request;
    }
    
}
```

#####Add some of this constructors to use Volley Response listeners:
```java
public TestJsonRequest(Response.Listener<JSONObject> responseListener,
            Response.ErrorListener errorListener) {
    super(responseListener, errorListener);
}

public TestJsonRequest(Void dataObject, Response.Listener<JSONObject> responseListener,
            Response.ErrorListener errorListener) {
    super(dataObject, responseListener, errorListener);
}
```
