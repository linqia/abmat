package com.linqia.abmat;

import java.io.File;
import java.io.IOException;

public class ProjectTester {

    public static void main(String[] args) throws IOException {
        Project thisProject = new Project(new File("pom.xml"));
        thisProject.install();
    }
}
