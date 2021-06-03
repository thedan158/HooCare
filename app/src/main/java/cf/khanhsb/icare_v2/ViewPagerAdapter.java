package cf.khanhsb.icare_v2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String userEmail;
    public ViewPagerAdapter(@NonNull FragmentManager fm,String email) {
        super(fm);
        this.userEmail = email;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment(userEmail);
            case 1:
                return new ArchieveFragment();
            case 2:
                return new MealFragment();
            case 3:
                return new GymFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
