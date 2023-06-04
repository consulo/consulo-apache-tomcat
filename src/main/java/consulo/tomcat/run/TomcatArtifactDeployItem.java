/*
 * Copyright 2013-2017 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.tomcat.run;

import consulo.compiler.artifact.ArtifactPointer;

/**
 * @author VISTALL
 * @since 06.11.13.
 */
public class TomcatArtifactDeployItem
{
	private ArtifactPointer myArtifactPointer;
	private String myPath;

	public TomcatArtifactDeployItem(ArtifactPointer artifactPointer, String path)
	{
		myArtifactPointer = artifactPointer;
		myPath = path;
	}

	public void setPath(String path)
	{
		myPath = path;
	}

	public String getPath()
	{
		return myPath;
	}

	public ArtifactPointer getArtifactPointer()
	{
		return myArtifactPointer;
	}

	@Override
	public TomcatArtifactDeployItem clone()
	{
		return new TomcatArtifactDeployItem(myArtifactPointer, myPath);
	}
}
