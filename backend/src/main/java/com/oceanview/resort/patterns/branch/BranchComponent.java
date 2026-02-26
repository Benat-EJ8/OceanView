package com.oceanview.resort.patterns.branch;

import com.oceanview.resort.domain.Branch;

import java.util.List;


 // Composite: Branch hierarchy - branch can have children.

public interface BranchComponent {
    Branch getBranch();
    List<BranchComponent> getChildren();
    boolean isComposite();
}
