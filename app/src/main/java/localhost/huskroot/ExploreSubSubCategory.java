package localhost.huskroot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class ExploreSubSubCategory extends AppCompatActivity {

    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String[][] action = {{bundle.getString("subCategory_id")}};
            ExploreSubSubWorker exploreSubSubWorker = new ExploreSubSubWorker(this);
            exploreSubSubWorker.execute(action);
            getSupportActionBar().setTitle("Explore / " + bundle.getString("category_name") + " / " + bundle.getString("subCategory_name"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            case R.id.refresh:
                finish(); startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSubSubCategories(final String[][] subSubCategories) {
        SubSubCategoryAdapter subSubCategoryAdapter = new SubSubCategoryAdapter(this, subSubCategories);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(subSubCategoryAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ExploreSubSubCategory.this, ListProduct.class);
                Bundle bundle = new Bundle();
                bundle.putString("subSubCategory_id", subSubCategories[position][0]);
                bundle.putString("subSubCategory_name", subSubCategories[position][1]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
