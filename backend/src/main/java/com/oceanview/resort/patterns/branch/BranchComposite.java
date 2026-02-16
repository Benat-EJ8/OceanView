package com.oceanview.resort.patterns.branch;

import com.oceanview.resort.domain.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchComposite implements BranchComponent {
    private final Branch branch;
    private final List<BranchComponent> children = new ArrayList<>();

    public BranchComposite(Branch branch) {
        this.branch = branch;
    }

    @Override
    public Branch getBranch() {
        return branch;
    }

    @Override
    public List<BranchComponent> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    public void addChild(BranchComponent child) {
        if (child != null) children.add(child);
    }
}
