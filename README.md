# AsyncLoading
------

> This project can help you load Image in ListView. Thanks to my teacher Rio who helped me restructured this project.

It will not download or load Image until slide is stop. And it will only load Image on visible screen area.

<img src="introduce.gif"/>

And you can use like this.

> This is not easy because you should write adapter by yourself, and of course, you can extends out AsyncLoadingListAdapter and do someting. So it chould be easy when you use it. And we are making it used easily.

```java
listView.setAdapter(new AsyncLoadingListAdapter(context) {
            private List<List<ImageResource>> imageResources;

            @Override
            public Collection<ImageResource> getImageResources(int position) {
                if (null == imageResources) {
                    imageResources = new ArrayList<>();

                    int count = getCount();
                    for (int i = 0; i < count; i++) {
                        ImageResource imageResource = new ImageResource();
                        imageResource.setPosition(i);
                        imageResource.setUrl(data[i]);

                        List<ImageResource> resources = new ArrayList<>();
                        resources.add(imageResource);

                        imageResources.add(resources);
                    }
                }
                return imageResources.get(position);
            }

            /**
             * Load over
             * @param imageResource ImageResource对象
             */
            @Override
            public void onLoadEnd(ImageResource imageResource) {
                if (ImageResource.LOAD_STATE_LOADED != imageResource.getLoadState())
                    return;

                int position = imageResource.getPosition();
                int firstVisibleItem = listView.getFirstVisiblePosition();
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (position < firstVisibleItem || position > lastVisiblePosition)
                    return;

                View view = listView.getChildAt(position - firstVisibleItem);
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                imageView.setImageBitmap(mBitmapLoader.get(imageResource.getUrl()));
                setAnimation(imageView);
            }

            @Override
            public int getCount() {
                return data.length;
            }

            @Override
            public Object getItem(int position) {
                return data[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (null == convertView) {
                    view = layoutInflater.inflate(R.layout.list_item, null);
                }
                // do setText and setImage operation
                return view;
            }
        });

        listView.setOnScrollListener(listView.new OnScrollListenerImpl());
```
