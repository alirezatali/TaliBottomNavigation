package com.alitali.talibottomnavigation;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
public class TaliBottomNavigationView extends BottomNavigationView {

    public TaliBottomNavigationView(Context context) {
        super(context);
    }

    public TaliBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaliBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param navGraphIds Navigation graph id's that must be connect to BottomNavigationView
     * @param fragmentManager Support fragment manager
     * @param containerId fragment container id (Must be : androidx.fragment.app.FragmentContainerView)
     * @param intent The current activity intent
     * @return
     */
    @NonNull
    public final LiveData<NavController> setupWithNavController(
            @NonNull List<Integer> navGraphIds,
            @NonNull final FragmentManager fragmentManager,
            int containerId,
            @NonNull Intent intent) {
        // Map of tags
        final SparseArray<String> graphIdToTagMap = new SparseArray<>();
        // Result. Mutable live data with the selected controlled
        final MutableLiveData<NavController> selectedNavController = new MutableLiveData<>();

        final int[] firstFragmentGraphId = {0};

        // First create a NavHostFragment for each NavGraph ID
        for(int navGraphIdsIndex = 0; navGraphIdsIndex < navGraphIds.size();navGraphIdsIndex++) {
            int item = navGraphIds.get(navGraphIdsIndex);
            String fragmentTag = getFragmentTag(navGraphIdsIndex);
            // Find or create the Navigation host fragment
            NavHostFragment navHostFragment = obtainNavHostFragment(fragmentManager, fragmentTag, item, containerId);
            // Obtain its id
            int graphId = navHostFragment.getNavController().getGraph().getId();

            if (navGraphIdsIndex == 0) {
                firstFragmentGraphId[0] = graphId;
            }

            // Save to the map
            graphIdToTagMap.put(graphId, fragmentTag);
            // Attach or detach nav host fragment depending on whether it's the selected item.
            if (getSelectedItemId() == graphId) {
                // Update livedata with the selected graph
                selectedNavController.setValue(navHostFragment.getNavController());
                attachNavHostFragment(fragmentManager, navHostFragment, navGraphIdsIndex == 0);
            } else {
                detachNavHostFragment(fragmentManager, navHostFragment);
            }

        }
         // Now connect selecting an item with swapping Fragments
        final String[] selectedItemTag = {graphIdToTagMap.get(getSelectedItemId())};
        String firstFragmentTag = graphIdToTagMap.get(firstFragmentGraphId[0]);

        final Boolean[] isOnFirstFragment = {selectedItemTag[0] != null && selectedItemTag[0].equals(firstFragmentTag)};
        // When a navigation item is selected
        setOnNavigationItemSelectedListener(item -> {
            boolean resultBool = false;

            // Don't do anything if the state is state has already been saved.
            if (!fragmentManager.isStateSaved()) {
                String newlySelectedItemTag = graphIdToTagMap.get(item.getItemId());
                if (selectedItemTag[0] == null || !selectedItemTag[0].equals(newlySelectedItemTag) ) {
                    // Pop everything above the first fragment (the "fixed start destination")
                    fragmentManager.popBackStack(firstFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    Fragment fragmentByTag = fragmentManager.findFragmentByTag(newlySelectedItemTag);
                    if (fragmentByTag == null) {
                        return false;
                    }

                    NavHostFragment selectedFragment = (NavHostFragment)fragmentByTag;

                    // Exclude the first fragment tag because it's always in the back stack.
                    if (!firstFragmentTag.equals(newlySelectedItemTag)) {
                        // Commit a transaction that cleans the back stack and adds the first fragment
                        // to it, creating the fixed started destination.
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                                R.anim.nav_default_enter_anim,
                                R.anim.nav_default_exit_anim,
                                R.anim.nav_default_pop_enter_anim,
                                R.anim.nav_default_pop_exit_anim)
                                .attach(selectedFragment)
                                .setPrimaryNavigationFragment(selectedFragment);

                        // Detach all other Fragments
                        for(int j = 0;j< graphIdToTagMap.size(); j++) {
                            graphIdToTagMap.keyAt(j);
                            String fragmentTagIter = graphIdToTagMap.valueAt(j);
                            if (!fragmentTagIter.equals(newlySelectedItemTag)) {
                                fragmentTransaction.detach(fragmentManager.findFragmentByTag(firstFragmentTag));
                            }
                        }

                        fragmentTransaction.addToBackStack(firstFragmentTag).setReorderingAllowed(true).commit();
                    }

                    selectedItemTag[0] = newlySelectedItemTag;
                    isOnFirstFragment[0] = selectedItemTag[0] == firstFragmentTag;
                    selectedNavController.setValue(selectedFragment.getNavController());
                    resultBool = true;
                }
            }

            return resultBool;
        });

        // Optional: on item reselected, pop back stack to the destination of the graph
        setupItemReselected(graphIdToTagMap, fragmentManager);
        // Handle deep link
        setupDeepLinks(navGraphIds, fragmentManager, containerId, intent);
        // Finally, ensure that we update our BottomNavigationView when the back stack changes
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (!isOnFirstFragment[0]) {

                if (!isOnBackStack(fragmentManager, firstFragmentTag)) {
                    setSelectedItemId(firstFragmentGraphId[0]);
                }
            }
            // Reset the graph if the currentDestination is not valid (happens when the back
            // stack is popped after using the back button).
            NavController navControllerF = selectedNavController.getValue();
            if (navControllerF != null) {
                if (navControllerF.getCurrentDestination() == null) {
                    navControllerF.navigate( navControllerF.getGraph().getId());
                }
            }

        });
        return selectedNavController;
    }

    private  void setupDeepLinks(List<Integer> navGraphIds,
                                 FragmentManager fragmentManager,
                                 int containerId, Intent intent) {
        for(int i = 0; i < navGraphIds.size();i++){
            String fragmentTag = getFragmentTag(i);
            // Find or create the Navigation host fragment
            NavHostFragment navHostFragment = obtainNavHostFragment(fragmentManager, fragmentTag, navGraphIds.get(i), containerId);
            // Handle Intent
            if (navHostFragment.getNavController().handleDeepLink(intent)) {
                int selectedItemId = getSelectedItemId();
                NavController navController = navHostFragment.getNavController();
                NavGraph navGraph = navController.getGraph();
                if (selectedItemId != navGraph.getId()) {
                    navController = navHostFragment.getNavController();
                    navGraph = navController.getGraph();
                    setSelectedItemId(navGraph.getId());
                }
            }
        }
    }
    private void setupItemReselected(final SparseArray<String> graphIdToTagMap,
                                     final FragmentManager fragmentManager) {
        setOnNavigationItemReselectedListener(item -> {
            String newlySelectedItemTag = graphIdToTagMap.get(item.getItemId());
            Fragment fragment = fragmentManager.findFragmentByTag(newlySelectedItemTag);
            if (fragment == null) {
                throw new RuntimeException("null cannot be cast to non-null type androidx.navigation.fragment.NavHostFragment");
            } else {
                NavHostFragment selectedFragment = (NavHostFragment)fragment;
                NavController navController = selectedFragment.getNavController();
                NavGraph navGraph = navController.getGraph();
                // Pop the back stack to the start destination of the current navController graph
                navController.popBackStack(navGraph.getStartDestination(), false);
            }
        });
    }
    private static void detachNavHostFragment(FragmentManager fragmentManager, NavHostFragment navHostFragment) {
        fragmentManager.beginTransaction().detach((Fragment) navHostFragment).commitNow();
    }

    private static void attachNavHostFragment(FragmentManager fragmentManager,
                                              NavHostFragment navHostFragment,
                                              boolean isPrimaryNavFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().attach(navHostFragment);

        if (isPrimaryNavFragment) {
            fragmentTransaction.setPrimaryNavigationFragment(navHostFragment);
        }

        fragmentTransaction.commitNow();
    }

    private static NavHostFragment obtainNavHostFragment(FragmentManager fragmentManager,
                                                         String fragmentTag,
                                                         int navGraphId,
                                                         int containerId) {
        NavHostFragment existingFragment = (NavHostFragment) fragmentManager.findFragmentByTag(fragmentTag);
        // If the Nav Host fragment exists, return it
        if (existingFragment != null) {
            return existingFragment;
        } else {  // Otherwise, create it and return it.
            NavHostFragment navHostFragment = NavHostFragment.create(navGraphId);
            fragmentManager.beginTransaction().add(containerId, (Fragment) navHostFragment, fragmentTag).commitNow();
            return navHostFragment;
        }
    }

    private static boolean isOnBackStack(@NonNull FragmentManager isOnBackStack, String backStackName) {
        int backStackCount = isOnBackStack.getBackStackEntryCount();
        int index = 0;

        for (int i = backStackCount; index < i; ++index) {
            FragmentManager.BackStackEntry backStackEntryAt = isOnBackStack.getBackStackEntryAt(index);

            if (Objects.equals(backStackEntryAt.getName(), backStackName)) {
                return true;
            }
        }

        return false;
    }

    private static String getFragmentTag(int index) {
        return "bottomNavigation#" + index;
    }
}