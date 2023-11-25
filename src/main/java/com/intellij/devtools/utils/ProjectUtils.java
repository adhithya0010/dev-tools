package com.intellij.devtools.utils;

import com.intellij.openapi.project.Project;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectUtils {

  @Getter @Setter private static Project project;
}
