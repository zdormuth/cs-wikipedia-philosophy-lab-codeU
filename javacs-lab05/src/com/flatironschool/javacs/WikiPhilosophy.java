package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy
{
	// the one WikiFetcher object used to handle all requests
	// it encapsulates the code and measures the time between requests
	static ArrayList<String> Urls = new ArrayList<String>();
	final static WikiFetcher wf = new WikiFetcher();
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException 
	{	
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		find(url);
	}
	
	public static void find(String url) throws IOException
	{
		if (!(url.equals("https://en.wikipedia.org/wiki/Philosophy")))
		{
			Elements paragraphs = wf.fetchWikipedia(url);
			for (int i = 0; i < paragraphs.size(); i++)
			{
				Element Para = paragraphs.get(i);
				String str = "";
				Iterable<Node> iter = new WikiNodeIterable(Para);
				for (Node node: iter)
				{
					if (node instanceof TextNode)
					{
						str = str + node;
					}
					if (node instanceof Element)
					{
						if (((Element) node).tagName() == "a" && !(node.attr("abs:href")).contains("#"))
						{
							url = node.attr("abs:href");
							//System.out.println(str);
							if(!(isParenthesisOkay(str)))
							{
								continue;
							}
							if (!(isNew(url)))
							{
								System.out.println("Error: Already visited page");
								return;
							}
							Urls.add(url);
							System.out.println(url);
							find(url);
							return;
						}
					}
				}
			}	
		}
		if (url.equals("https://en.wikipedia.org/wiki/Philosophy"))
		{
			System.out.println(url);
			return;
		}
		System.out.println("Something went wrong!");
	}
	
	public static boolean isNew(String url)
	{
		for (int i = 0; i < Urls.size(); i++)
		{
			if (Urls.get(i).equals(url))
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isParenthesisOkay(String b)
	{
		int count = 0;
		for (int i = 0; i < b.length(); i++)
		{
			if (b.charAt(i) == '(')
			{
				count++;
			}
			else if (b.charAt(i) == ')')
			{
				count--;
			}
		}
		if (count == 0)
		{
			return true;
		}
		return false;
	}

	private static void iterativeDFS(Node root)
	{
		Deque<Node> stack = new ArrayDeque<Node>();
		stack.push(root);

		while (!stack.isEmpty())
		{
			Node node = stack.pop();
			if (node instanceof TextNode)
			{
				System.out.print(node);
			}
		
			// push children onto stack in reverse order after parent is popped
			List<Node> nodes = new ArrayList<Node>(node.childNodes());
			Collections.reverse(nodes);

			for (Node child: nodes)
			{
				stack.push(child);
			}
		}
	}

	private static void recursiveDFS(Node node)
	{
		if (node instanceof TextNode)
		{
			System.out.print(node);
		}
		for (Node child: node.childNodes())
		{
			recursiveDFS(child);
		}
	}
}