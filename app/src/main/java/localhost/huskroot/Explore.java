package localhost.huskroot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class Explore extends AppCompatActivity {

    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ExploreWorker exploreWorker = new ExploreWorker(this);
        exploreWorker.execute();
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

    public void setCategories(final String[][] categories){

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categories);
        grid = (GridView)findViewById(R.id.grid);
        grid.setAdapter(categoryAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void  onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(Explore.this, ExploreSubCategory.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_id",categories[position][0]);
                bundle.putString("category_name",categories[position][1]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
