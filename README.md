Volley Request Manager
======================

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
        .addRequest(new TestJsonRequest(mRequestObserver))
        .start();    
```

#####Custom listener implementation :
```java
private RequestObserver mRequestObserver = new RequestObserver<JSONObject, Void>() {
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
